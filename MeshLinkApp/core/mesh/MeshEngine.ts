import { DBManager } from '../database/dbManager';
import { EventEmitter } from 'expo-modules-core';

// Temporary mock/fallback for MeshEngine before Native Transport resolves.
export class MeshEngine {
  private static instance: MeshEngine;
  private db: DBManager;
  private myDeviceId: string = "ML-" + Math.random().toString(36).substring(2, 7).toUpperCase();
  private transportModule: any;
  private emitter: EventEmitter | null = null;
  private uiCallbacks: Set<(msg: any) => void> = new Set();

  private constructor() {
    this.db = DBManager.getInstance();
  }

  public subscribeToMessages(callback: (msg: any) => void) {
    this.uiCallbacks.add(callback);
    return () => this.uiCallbacks.delete(callback);
  }

  private notifyUI(msg: any) {
    this.uiCallbacks.forEach(cb => cb(msg));
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
       this.transportModule = require('../../modules/mesh-transport').default;
       if (this.transportModule) {
           this.emitter = new EventEmitter(this.transportModule);

           this.emitter.addListener('onPeerDiscovered', (event) => {
               console.log('[MeshEngine] Peer Discovered:', event);
               this.handlePeerDiscovered(event);
           });

           this.emitter.addListener('onMessageReceived', (event) => {
               console.log('[MeshEngine] Message Received:', event);
               this.handleIncomingMessage(event);
           });

           if (this.transportModule.startAdvertising) {
              this.transportModule.startAdvertising(this.myDeviceId, "BOTH");
              this.transportModule.startScanning();
           }
       }
    } catch(e) {
      console.warn("Transport init failed:", e);
    }
  }

  private async handlePeerDiscovered(peer: any) {
    // peer: { address, rssi, type, payload }
    // In our BLE implementation, payload is the deviceId
    const deviceId = peer.payload || peer.address;
    // Update DB
    // await this.db.updatePeer(deviceId, peer.address, Date.now());
  }

  private async handleIncomingMessage(event: any) {
    const { payload } = event;
    try {
        const data = JSON.parse(payload);
        // Relay logic: if (data.receiver_id !== this.myDeviceId && data.ttl > 0) { relay(data) }
        await this.db.storeMessage(data.id, data.sender_id, data.receiver_id, data.group_id, data.content, data.ttl - 1, 'received');
        this.notifyUI(data);
    } catch(e) {}
  }

  public async sendDirect(receiverId: string, content: string) {
    const messageId = Date.now().toString(); 
    const ttl = 5;
    
    await this.db.storeMessage(messageId, 'SELF', receiverId, null, content, ttl, 'pending');

    const payloadBytes = JSON.stringify({ id: messageId, sender_id: this.myDeviceId, receiver_id: receiverId, content: content, ttl: ttl });
    
    try {
       if (this.transportModule && this.transportModule.broadcastMessage) {
          await this.transportModule.broadcastMessage(payloadBytes);
       } else {
          console.log("Mock broadcast sent (no native module):", payloadBytes);
       }
    } catch(e) {
       console.log("Mock broadcast sent (error):", payloadBytes);
    }
    
    await this.db.storeMessage(messageId, 'SELF', receiverId, null, content, ttl, 'sent');
  }

  // Simulation methods
  public simulateIncomingPeer(deviceId: string) {
      if (this.emitter) {
          // Manually emit event if supported by expo-modules-core EventEmitter,
          // but usually EventEmitter is for listening.
          // We can just call the handler directly for simulation.
          this.handlePeerDiscovered({ address: '00:11:22:33:44:55', rssi: -50, type: 'BLE', payload: deviceId });
      }
  }

  public simulateIncomingMessage(senderId: string, content: string) {
      const mockEvent = {
          payload: JSON.stringify({
              id: 'sim-' + Date.now(),
              sender_id: senderId,
              receiver_id: this.myDeviceId,
              content: content,
              ttl: 5
          })
      };
      this.handleIncomingMessage(mockEvent);
  }
}
