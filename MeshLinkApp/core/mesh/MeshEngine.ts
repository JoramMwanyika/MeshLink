import { DBManager } from '../database/dbManager';

// Temporary mock/fallback for MeshEngine before Native Transport resolves.
export class MeshEngine {
  private static instance: MeshEngine;
  private db: DBManager;
  private myDeviceId: string = "TEMP_SELF_UUID";

  private constructor() {
    this.db = DBManager.getInstance();
  }

  public static getInstance(): MeshEngine {
    if (!MeshEngine.instance) {
      MeshEngine.instance = new MeshEngine();
    }
    return MeshEngine.instance;
  }

  public async init() {
    await this.db.initDB();
    console.log('[MeshEngine] Initialized logic.');
    try {
       const MeshTransportModule = require('../../modules/mesh-transport').default;
       if (MeshTransportModule && MeshTransportModule.startAdvertising) {
           MeshTransportModule.startAdvertising(this.myDeviceId, "BOTH"); // Tells Kotlin to spin up BLE & Local Hotspot
           MeshTransportModule.startScanning();
       }
    } catch(e) {}
  }

  public async sendDirect(receiverId: string, content: string) {
    const messageId = Date.now().toString(); 
    const ttl = 5;
    
    await this.db.storeMessage(messageId, this.myDeviceId, receiverId, null, content, ttl, 'pending');

    const payloadBytes = JSON.stringify({ id: messageId, sender_id: this.myDeviceId, receiver_id: receiverId, content: content, ttl: ttl });
    
    try {
       const MeshTransportModule = require('../../modules/mesh-transport').default;
       await MeshTransportModule.broadcastMessage(payloadBytes);
    } catch(e) {
       console.log("Mock broadcast sent", payloadBytes);
    }
    
    await this.db.storeMessage(messageId, this.myDeviceId, receiverId, null, content, ttl, 'sent');
  }
}
