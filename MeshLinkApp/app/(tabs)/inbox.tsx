import { View, Text, StyleSheet, FlatList, TouchableOpacity } from 'react-native';
import { useState } from 'react';

const MESSAGES = [
  { id: '1', name: 'Sarah', snippet: 'Hey! Are you coming to the...', time: '10:24 AM', type: 'unread' },
  { id: '2', name: 'Team Alpha', snippet: 'Meeting point updated.', time: '09:11 AM', type: 'unread' },
  { id: '3', name: 'Rescue Unit', snippet: 'Stay safe everyone.', time: 'Yesterday', type: 'unread' },
  { id: '4', name: 'Mike', snippet: 'Got the supplies.', time: 'Yesterday', type: 'sent' }
];

const TABS = ['All', 'Unread', 'Sent', 'Failed'];

export default function InboxScreen() {
  const [activeTab, setActiveTab] = useState('All');

  const filteredMsgs = MESSAGES.filter(m => activeTab === 'All' || m.type === activeTab.toLowerCase());

  return (
    <View style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.headerTitle}>Inbox</Text>
      </View>

      <View style={styles.tabsContainer}>
        {TABS.map(t => (
           <TouchableOpacity 
             key={t} 
             style={[styles.tabBtn, activeTab === t && styles.tabActive]}
             onPress={() => setActiveTab(t)}
           >
             <Text style={[styles.tabText, activeTab === t && styles.tabTextActive]}>{t}</Text>
           </TouchableOpacity>
        ))}
      </View>

      <FlatList
        data={filteredMsgs}
        keyExtractor={i => i.id}
        renderItem={({ item }) => (
          <TouchableOpacity style={styles.msgRow}>
             <View style={styles.iconBox}>
                <Text style={styles.iconTxt}>📄</Text>
             </View>
             <View style={styles.contentBox}>
                <Text style={styles.msgTitle}>From: {item.name}</Text>
                <Text style={styles.msgSnippet}>{item.snippet}</Text>
             </View>
             <View style={styles.metaBox}>
                <Text style={styles.timeTxt}>{item.time}</Text>
                <Text style={styles.chevron}>›</Text>
             </View>
          </TouchableOpacity>
        )}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#0B0F19' },
  header: { padding: 20, paddingTop: 60, paddingBottom: 10 },
  headerTitle: { fontSize: 24, fontWeight: 'bold', color: '#FFF' },
  tabsContainer: { flexDirection: 'row', paddingHorizontal: 20, marginBottom: 20, gap: 10 },
  tabBtn: { paddingVertical: 8, paddingHorizontal: 16, borderRadius: 20 },
  tabActive: { backgroundColor: '#00FF66' },
  tabText: { color: '#8895AA', fontWeight: 'bold' },
  tabTextActive: { color: '#000' },
  msgRow: { flexDirection: 'row', padding: 20, borderBottomWidth: 1, borderBottomColor: '#1A2130', alignItems: 'center' },
  iconBox: { width: 40, height: 40, borderRadius: 8, backgroundColor: '#1A2130', justifyContent: 'center', alignItems: 'center', marginRight: 15 },
  iconTxt: { fontSize: 18 },
  contentBox: { flex: 1 },
  msgTitle: { color: '#FFF', fontSize: 15, fontWeight: 'bold', marginBottom: 4 },
  msgSnippet: { color: '#8895AA', fontSize: 14 },
  metaBox: { alignItems: 'flex-end', justifyContent: 'space-between', height: 40 },
  timeTxt: { color: '#8895AA', fontSize: 12 },
  chevron: { color: '#00FF66', fontSize: 20, fontWeight: 'bold' }
});
