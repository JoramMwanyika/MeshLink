import { Platform } from 'react-native';
import { DATABASE_NAME, INIT_QUERIES } from './schema';

let SQLite: any = null;
if (Platform.OS !== 'web') {
  try {
    SQLite = require('expo-sqlite');
  } catch (e) {}
}

export class DBManager {
  private db: any = null; // Removed strict type for dynamic import
  private static instance: DBManager;
  private memoryDb: any[] = [];

  private constructor() {}

  public static getInstance(): DBManager {
    if (!DBManager.instance) {
      DBManager.instance = new DBManager();
    }
    return DBManager.instance;
  }

  public async initDB() {
    if (Platform.OS === 'web' || !SQLite) {
       console.log('[DBManager] Web Mode active. Using Memory DB.');
       return;
    }
    try {
        this.db = await SQLite.openDatabaseAsync(DATABASE_NAME);
        for (const query of INIT_QUERIES) {
          await this.db.execAsync(query);
        }
        console.log('[DBManager] DB Initialized.');
    } catch(e) {
        console.warn("DB init failed, likely native mismatch in Expo Go", e);
    }
  }

  public async storeMessage(id: string, senderId: string, receiverId: string | null, groupId: string | null, content: string, ttl: number, status: string) {
    const timestamp = Date.now();
    if (!this.db) {
        this.memoryDb.push({ id, sender_id: senderId, receiver_id: receiverId, group_id: groupId, content, timestamp, ttl, status });
        return;
    }
    try {
        await this.db.runAsync(
        `INSERT OR IGNORE INTO messages (id, sender_id, receiver_id, group_id, content, timestamp, ttl, status)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?)`,
        [id, senderId, receiverId, groupId, content, timestamp, ttl, status]
        );
    } catch(e){}
  }

  public async getMessage(id: string) {
    if (!this.db) {
        return this.memoryDb.find(m => m.id === id) || null;
    }
    return await this.db.getFirstAsync(`SELECT * FROM messages WHERE id = ?`, [id]);
  }

  public async getMessagesForChat(chatId: string) {
    if (!this.db) {
        return this.memoryDb.filter(m => m.sender_id === chatId || m.receiver_id === chatId)
                            .sort((a,b) => a.timestamp - b.timestamp);
    }
    try {
        return await this.db.getAllAsync(
            `SELECT * FROM messages
             WHERE sender_id = ? OR receiver_id = ?
             ORDER BY timestamp ASC`,
            [chatId, chatId]
        );
    } catch(e) {
        return [];
    }
  }

  public async getAllRecentChats() {
    if (!this.db) {
        // Simple mock for memory db
        const peers = new Set(this.memoryDb.map(m => m.sender_id === 'SELF' ? m.receiver_id : m.sender_id));
        return Array.from(peers).map(p => ({
            peer_id: p,
            content: 'Last message...',
            timestamp: Date.now()
        }));
    }
    try {
        return await this.db.getAllAsync(
            `SELECT DISTINCT
                CASE WHEN sender_id = 'SELF' THEN receiver_id ELSE sender_id END as peer_id,
                content, timestamp
             FROM messages
             GROUP BY peer_id
             ORDER BY timestamp DESC`
        );
    } catch(e) {
        return [];
    }
  }
}
