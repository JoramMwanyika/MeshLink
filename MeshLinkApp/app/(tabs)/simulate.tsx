import { View, Text, StyleSheet, TouchableOpacity, ScrollView } from 'react-native';
import { MeshEngine } from '../../core/mesh/MeshEngine';
import { useState } from 'react';

export default function SimulateScreen() {
  const [logs, setLogs] = useState<string[]>([]);

  const addLog = (msg: string) => {
    setLogs(prev => [msg, ...prev].slice(0, 10));
  };

  const simulatePeer = () => {
    const id = 'PEER-' + Math.floor(Math.random() * 1000);
    MeshEngine.getInstance().simulateIncomingPeer(id);
    addLog(`Simulated discovery of ${id}`);
  };

  const simulateMsg = () => {
    const id = 'SARAH-MOCK';
    const contents = [
        "Hello from the mesh!",
        "Can you hear me?",
        "Relaying message through node 4...",
        "I'm at the rendezvous point."
    ];
    const msg = contents[Math.floor(Math.random() * contents.length)];
    MeshEngine.getInstance().simulateIncomingMessage(id, msg);
    addLog(`Simulated message from ${id}: ${msg}`);
  };

  return (
    <View style={styles.container}>
      <Text style={styles.header}>Mesh Simulator</Text>
      <Text style={styles.subtext}>Use these tools to test the app logic without physical devices.</Text>

      <TouchableOpacity style={styles.button} onPress={simulatePeer}>
        <Text style={styles.buttonText}>Simulate Peer Discovery</Text>
      </TouchableOpacity>

      <TouchableOpacity style={styles.button} onPress={simulateMsg}>
        <Text style={styles.buttonText}>Simulate Incoming Message</Text>
      </TouchableOpacity>

      <View style={styles.logContainer}>
        <Text style={styles.logHeader}>Event Log</Text>
        <ScrollView style={styles.logScroll}>
            {logs.map((log, i) => (
                <Text key={i} style={styles.logText}>{`> ${log}`}</Text>
            ))}
            {logs.length === 0 && <Text style={styles.logText}>No events yet.</Text>}
        </ScrollView>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#0B0F19', padding: 20, paddingTop: 60 },
  header: { fontSize: 24, fontWeight: 'bold', color: '#FFF', marginBottom: 10 },
  subtext: { color: '#8895AA', marginBottom: 30 },
  button: { backgroundColor: '#1C2536', padding: 18, borderRadius: 12, marginBottom: 15, borderWidth: 1, borderColor: '#00FF6633' },
  buttonText: { color: '#00FF66', fontWeight: 'bold', fontSize: 16, textAlign: 'center' },
  logContainer: { flex: 1, marginTop: 20, backgroundColor: '#050B14', borderRadius: 12, padding: 15 },
  logHeader: { color: '#4F5E7B', fontWeight: 'bold', marginBottom: 10, fontSize: 12, textTransform: 'uppercase' },
  logScroll: { flex: 1 },
  logText: { color: '#8895AA', fontSize: 13, marginBottom: 5, fontFamily: 'monospace' }
});
