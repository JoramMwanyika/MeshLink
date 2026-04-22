export type PeerDiscoveredEvent = {
  address: string;
  rssi: number;
  type: string;
  payload?: string;
};

export type TransportEvent = {
  type: string;
  code?: number;
};

export type MeshTransportModuleEvents = {
  onPeerDiscovered: (event: PeerDiscoveredEvent) => void;
  onTransportEvent: (event: TransportEvent) => void;
  onMessageReceived: (payload: string) => void;
};
