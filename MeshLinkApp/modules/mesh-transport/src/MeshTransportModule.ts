import { NativeModule, requireNativeModule } from 'expo';
import { MeshTransportModuleEvents } from './MeshTransport.types';

declare class MeshTransportModule extends NativeModule<MeshTransportModuleEvents> {
  startAdvertising(deviceId: string, mode: string): Promise<boolean>;
  startScanning(): Promise<boolean>;
  broadcastMessage(payload: string): Promise<boolean>;
}

let moduleToExport;

try {
  moduleToExport = requireNativeModule<MeshTransportModule>('MeshTransport');
} catch (error) {
  console.warn("⚠️ Native MeshTransport module not found. Falling back to mocks (Expo Go mode). Bluetooth will not work.");
  // Mock implementations for Expo Go / Web UI testing
  moduleToExport = {
    startAdvertising: async () => true,
    startScanning: async () => true,
    broadcastMessage: async (p: string) => { 
        console.log("Mock broadcast: ", p); 
        return true; 
    },
    addListener: () => ({ remove: () => {} }),
    removeAllListeners: () => {}
  } as any;
}

export default moduleToExport;
