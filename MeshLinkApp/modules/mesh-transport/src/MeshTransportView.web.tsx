import * as React from 'react';

import { MeshTransportViewProps } from './MeshTransport.types';

export default function MeshTransportView(props: MeshTransportViewProps) {
  return (
    <div>
      <iframe
        style={{ flex: 1 }}
        src={props.url}
        onLoad={() => props.onLoad({ nativeEvent: { url: props.url } })}
      />
    </div>
  );
}
