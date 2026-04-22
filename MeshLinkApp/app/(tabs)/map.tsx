import { View, Text, StyleSheet, TouchableOpacity } from 'react-native';

export default function MapScreen() {
  return (
    <View style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.headerTitle}>Network Map</Text>
        <TouchableOpacity style={styles.infoBtn}>
            <Text style={{color: '#FFF'}}>i</Text>
        </TouchableOpacity>
      </View>

      <View style={styles.tabsContainer}>
         <TouchableOpacity style={[styles.tabBtn, styles.tabActive]}><Text style={styles.tabTextActive}>Live</Text></TouchableOpacity>
         <TouchableOpacity style={styles.tabBtn}><Text style={styles.tabText}>History</Text></TouchableOpacity>
      </View>

      <View style={styles.mapArea}>
        {/* Draw Connection Lines */}
        <View style={[styles.line, { top: '35%', left: '30%', width: 100, transform: [{rotate: '45deg'}] }]} />
        <View style={[styles.line, { top: '35%', left: '46%', width: 100, transform: [{rotate: '-45deg'}] }]} />
        <View style={[styles.line, { top: '55%', left: '30%', width: 100, transform: [{rotate: '-45deg'}] }]} />
        <View style={[styles.line, { top: '55%', left: '46%', width: 100, transform: [{rotate: '45deg'}] }]} />
        <View style={[styles.line, { top: '75%', left: '49%', width: 50, transform: [{rotate: '90deg'}] }]} />

        {/* Nodes */}
        <View style={[styles.nodeBox, { top: '20%', left: '40%' }]}>
            <View style={styles.nodeMe} />
            <Text style={styles.nodeName}>Sarah (You)</Text>
        </View>
        
        <View style={[styles.nodeBox, { top: '50%', left: '15%' }]}>
            <View style={styles.nodePeer} />
            <Text style={styles.nodeName}>Alex</Text>
        </View>

        <View style={[styles.nodeBox, { top: '50%', right: '15%' }]}>
            <View style={styles.nodePeer} />
            <Text style={styles.nodeName}>Mike</Text>
        </View>

        <View style={[styles.nodeBox, { top: '50%', left: '40%' }]}>
            <View style={styles.nodeGroup} />
            <Text style={styles.nodeName}>Team Alpha</Text>
        </View>

        <View style={[styles.nodeBox, { top: '80%', left: '40%' }]}>
            <View style={styles.nodePeer} />
            <Text style={styles.nodeName}>Priya</Text>
        </View>
      </View>

      <View style={styles.legend}>
        <View style={styles.legendRow}>
            <View style={[styles.legendLine, {borderColor: '#00FF66'}]} /><Text style={styles.legendText}>Strong Connection</Text>
        </View>
        <View style={styles.legendRow}>
            <View style={[styles.legendLine, {borderColor: '#9B51E0'}]} /><Text style={styles.legendText}>Relay Node</Text>
        </View>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#0B0F19' },
  header: { padding: 20, paddingTop: 60, flexDirection: 'row', justifyContent: 'center', alignItems: 'center' },
  headerTitle: { fontSize: 20, fontWeight: 'bold', color: '#FFF' },
  infoBtn: { position: 'absolute', right: 20, top: 60, width: 24, height: 24, borderRadius: 12, borderWidth: 1, borderColor: '#FFF', justifyContent: 'center', alignItems: 'center' },
  tabsContainer: { flexDirection: 'row', justifyContent: 'center', marginVertical: 20, gap: 10 },
  tabBtn: { paddingVertical: 6, paddingHorizontal: 20, borderRadius: 20 },
  tabActive: { backgroundColor: '#00FF66' },
  tabText: { color: '#8895AA', fontWeight: 'bold' },
  tabTextActive: { color: '#000', fontWeight: 'bold' },
  mapArea: { flex: 1, position: 'relative' },
  line: { position: 'absolute', height: 2, backgroundColor: '#00FF66' },
  nodeBox: { position: 'absolute', alignItems: 'center', width: 80 },
  nodeMe: { width: 50, height: 50, borderRadius: 25, backgroundColor: '#00FF66', borderWidth: 2, borderColor: '#FFF' },
  nodePeer: { width: 50, height: 50, borderRadius: 25, backgroundColor: '#00FF66' },
  nodeGroup: { width: 50, height: 50, borderRadius: 25, backgroundColor: '#9B51E0' },
  nodeName: { color: '#FFF', marginTop: 8, fontSize: 13, fontWeight: 'bold', textAlign: 'center' },
  legend: { padding: 20, flexDirection: 'row', justifyContent: 'space-around', borderTopWidth: 1, borderTopColor: '#1A2130' },
  legendRow: { flexDirection: 'row', alignItems: 'center', gap: 10 },
  legendLine: { width: 30, height: 2, borderBottomWidth: 2 },
  legendText: { color: '#8895AA', fontSize: 12 }
});
