import { requireNativeView } from 'expo';
import * as React from 'react';

import { MeshTransportViewProps } from './MeshTransport.types';

const NativeView: React.ComponentType<MeshTransportViewProps> =
  requireNativeView('MeshTransport');

export default function MeshTransportView(props: MeshTransportViewProps) {
  return <NativeView {...props} />;
}
