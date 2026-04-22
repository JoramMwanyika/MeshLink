import { View, Text, StyleSheet, TouchableOpacity } from 'react-native';
import { useLocalSearchParams, useRouter } from 'expo-router';

export default function MessageDetailsScreen() {
  const { msgId } = useLocalSearchParams();
  const router = useRouter();

  return (
    <View style={styles.container}>
      <View style={styles.header}>
        <TouchableOpacity onPress={() => router.back()} style={styles.backBtn}>
          <Text style={{color:'#FFF', fontSize: 20}}>←</Text>
        </TouchableOpacity>
        <Text style={styles.title}>Message Details</Text>
        <TouchableOpacity style={styles.menuBtn}>
          <Text style={{color:'#FFF', fontSize: 20}}>⋮</Text>
        </TouchableOpacity>
      </View>

      <View style={styles.idCard}>
         <Text style={styles.idLabel}>Message ID</Text>
         <View style={{flexDirection: 'row', justifyContent: 'space-between'}}>
            <Text style={styles.idValue}>{msgId || 'ML-2024-05-17-00123'}</Text>
            <Text style={{color: '#8895AA'}}>📋</Text>
         </View>
      </View>

      <View style={styles.timeline}>
         {/* Step 1 */}
         <View style={styles.timelineStep}>
            <View style={styles.nodeActive} />
            <View style={styles.timelineContent}>
               <Text style={styles.stepTitle}>Sent</Text>
               <Text style={styles.stepDesc}>By you</Text>
            </View>
            <Text style={styles.stepTime}>10:20 AM</Text>
         </View>

         {/* Step 2 */}
         <View style={styles.timelineStep}>
            <View style={styles.nodeActive} />
            <View style={styles.timelineContent}>
               <Text style={styles.stepTitle}>Relayed</Text>
               <Text style={styles.stepDesc}>By Alex</Text>
            </View>
            <Text style={styles.stepTime}>10:21 AM</Text>
         </View>

         {/* Step 3 */}
         <View style={styles.timelineStep}>
            <View style={styles.nodeActive} />
            <View style={styles.timelineContent}>
               <Text style={styles.stepTitle}>Relayed</Text>
               <Text style={styles.stepDesc}>By Priya</Text>
            </View>
            <Text style={styles.stepTime}>10:22 AM</Text>
         </View>

         {/* Step 4 */}
         <View style={styles.timelineStep}>
            <View style={styles.nodeActive} />
            <View style={styles.timelineContent}>
               <Text style={styles.stepTitle}>Delivered</Text>
               <Text style={styles.stepDesc}>To Mike</Text>
            </View>
            <Text style={styles.stepTime}>10:23 AM</Text>
         </View>

         {/* Vertical track drawn behind nodes */}
         <View style={styles.track} />
      </View>

      <View style={{flex: 1}} />

      <View style={styles.footer}>
         <Text style={styles.footerText}>This message was delivered via 2 relay(s).</Text>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#0B0F19' },
  header: { padding: 20, paddingTop: 60, flexDirection: 'row', alignItems: 'center', justifyContent: 'space-between' },
  backBtn: { padding: 5, width: 40 },
  title: { fontSize: 20, fontWeight: 'bold', color: '#FFF' },
  menuBtn: { padding: 5, width: 40, alignItems: 'flex-end' },
  idCard: { backgroundColor: '#1A2130', margin: 20, padding: 20, borderRadius: 12 },
  idLabel: { color: '#8895AA', fontSize: 13, marginBottom: 5 },
  idValue: { color: '#FFF', fontSize: 16, fontFamily: 'monospace' },
  timeline: { padding: 20, position: 'relative' },
  timelineStep: { flexDirection: 'row', alignItems: 'center', marginBottom: 40, zIndex: 2 },
  nodeActive: { width: 16, height: 16, borderRadius: 8, backgroundColor: '#00FF66', marginRight: 20 },
  timelineContent: { flex: 1 },
  stepTitle: { color: '#FFF', fontSize: 16, fontWeight: 'bold' },
  stepDesc: { color: '#8895AA', fontSize: 14, marginTop: 2 },
  stepTime: { color: '#8895AA', fontSize: 13 },
  track: { position: 'absolute', left: 27, top: 30, bottom: 50, width: 2, backgroundColor: '#00FF66', zIndex: 1 },
  footer: { backgroundColor: '#1A2130', padding: 20, marnTop: 'auto' },
  footerText: { color: '#8895AA', textAlign: 'center' }
});
