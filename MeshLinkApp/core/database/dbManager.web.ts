// Web-specific Mock to entirely bypass expo-sqlite Metro dependency resolution
export class DBManager {
  private static instance: DBManager;
  private constructor() {}

  public static getInstance(): DBManager {
    if (!DBManager.instance) {
      DBManager.instance = new DBManager();
    }
    return DBManager.instance;
  }

  public async initDB() {
    console.log('[DBManager Web Mock] SQLite successfully bypassed for Browser UI testing.');
  }

  public async storeMessage(...args: any[]) { }
  public async getMessage(id: string) { return null; }
  public async createGroup(...args: any[]) { }
  public async getPendingMessages() { return []; }
}
