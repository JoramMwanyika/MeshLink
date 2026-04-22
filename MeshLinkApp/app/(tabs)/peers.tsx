import { View, Text, StyleSheet, FlatList, Animated, Easing } from 'react-native';
import { useEffect, useRef, useState } from 'react';

const MOCK_PEERS = [
  { id: '1', name: 'Alex', deviceId: 'ML-A1B2-C3D4', distance: '2 m' },
  { id: '2', name: 'Priya', deviceId: 'ML-9F8E-7D6C', distance: '4 m' }
];

export default function PeersScreen() {
  const spinValue = useRef(new Animated.Value(0)).current;

  useEffect(() => {
    Animated.loop(
      Animated.timing(spinValue, { toValue: 1, duration: 3000, easing: Easing.linear, useNativeDriver: true })
    ).start();
  }, []);

  const spin = spinValue.interpolate({ inputRange: [0, 1], outputRange: ['0deg', '360deg'] });

  return (
    <View style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.headerTitle}>Nearby Devices</Text>
        <Text style={styles.subtext}>Scanning for nearby peers...</Text>
      </View>

      <View style={styles.radarContainer}>
        <View style={styles.radarOuter}>
          <View style={styles.radarMiddle}>
              <View style={styles.radarInner} />
          </View>
        </View>
        <Animated.View style={[styles.sweeper, { transform: [{ rotate: spin }] }]} />
      </View>

      <View style={styles.listSection}>
        <Text style={styles.sectionTitle}>Nearby Peers</Text>
        
        <FlatList
          data={MOCK_PEERS}
          keyExtractor={(i) => i.id}
          renderItem={({ item }) => (
            <View style={styles.peerRow}>
              <View style={styles.avatar} />
              <View style={styles.peerInfo}>
                <Text style={styles.peerName}>{item.name}</Text>
                <Text style={styles.peerId}>{item.deviceId}</Text>
              </View>
              <View style={styles.signalBox}>
                <Text style={styles.distance}>{item.distance}</Text>
                <Text style={{color: '#00FF66'}}>📶</Text>
              </View>
            </View>
          )}
        />
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#0B0F19' },
  header: { padding: 20, paddingTop: 60 },
  headerTitle: { fontSize: 22, fontWeight: 'bold', color: '#FFF' },
  subtext: { color: '#8895AA', marginTop: 5 },
  radarContainer: { height: 250, justifyContent: 'center', alignItems: 'center', marginVertical: 20 },
  radarOuter: { width: 220, height: 220, borderRadius: 110, borderWidth: 1, borderColor: '#00FF6633', justifyContent: 'center', alignItems: 'center' },
  radarMiddle: { width: 140, height: 140, borderRadius: 70, borderWidth: 1, borderColor: '#00FF6655', justifyContent: 'center', alignItems: 'center' },
  radarInner: { width: 60, height: 60, borderRadius: 30, backgroundColor: '#00FF6622', borderWidth: 1, borderColor: '#00FF66' },
  sweeper: { position: 'absolute', width: 220, height: 220, borderRadius: 110, borderTopColor: '#00FF66', borderTopWidth: 2, borderRightColor: 'transparent', borderRightWidth: 110 },
  listSection: { flex: 1, paddingHorizontal: 20 },
  sectionTitle: { color: '#FFF', fontSize: 16, fontWeight: '600', marginBottom: 15 },
  peerRow: { flexDirection: 'row', alignItems: 'center', paddingVertical: 15, borderBottomWidth: 1, borderBottomColor: '#131B2A' },
  avatar: { width: 40, height: 40, borderRadius: 20, backgroundColor: '#9B51E0', marginRight: 15 },
  peerInfo: { flex: 1 },
  peerName: { color: '#FFF', fontSize: 16, fontWeight: 'bold' },
  peerId: { color: '#8895AA', fontSize: 12 },
  signalBox: { flexDirection: 'row', alignItems: 'center', gap: 10 },
  distance: { color: '#FFF', fontSize: 14 }
});
