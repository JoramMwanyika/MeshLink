import { registerWebModule, NativeModule } from 'expo';

import { ChangeEventPayload } from './MeshTransport.types';

type MeshTransportModuleEvents = {
  onChange: (params: ChangeEventPayload) => void;
}

class MeshTransportModule extends NativeModule<MeshTransportModuleEvents> {
  PI = Math.PI;
  async setValueAsync(value: string): Promise<void> {
    this.emit('onChange', { value });
  }
  hello() {
    return 'Hello world! 👋';
  }
};

export default registerWebModule(MeshTransportModule, 'MeshTransportModule');
