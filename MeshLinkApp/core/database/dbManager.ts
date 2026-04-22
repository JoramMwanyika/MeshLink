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

  private constructor() {}

  public static getInstance(): DBManager {
    if (!DBManager.instance) {
      DBManager.instance = new DBManager();
    }
    return DBManager.instance;
  }

  public async initDB() {
    if (Platform.OS === 'web' || !SQLite) {
       console.log('[DBManager] Web Mode active. Mocking SQLite.');
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
    if (!this.db) return;
    try {
        await this.db.runAsync(
        `INSERT OR IGNORE INTO messages (id, sender_id, receiver_id, group_id, content, timestamp, ttl, status)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?)`,
        [id, senderId, receiverId, groupId, content, Date.now(), ttl, status]
        );
    } catch(e){}
  }

  public async getMessage(id: string) {
    if (!this.db) return null;
    return await this.db.getFirstAsync(`SELECT * FROM messages WHERE id = ?`, [id]);
  }
}
