import { View, Text, StyleSheet, FlatList, TextInput, TouchableOpacity } from 'react-native';
import { useRouter } from 'expo-router';
import { useState } from 'react';

const FREQUENT_PEERS = [
  { id: '1', name: 'Sarah', status: 'Online (2m away)' },
  { id: '2', name: 'Mike', status: 'Online (5m away)' },
  { id: '3', name: 'Team Alpha', status: '3 members', isGroup: true }
];

const ALL_PEERS = [
  { id: '4', name: 'Alex', status: 'Online (1m away)' },
  { id: '5', name: 'Priya', status: 'Online (3m away)' }
];

export default function NewChatScreen() {
  const router = useRouter();
  const [search, setSearch] = useState('');

  const renderPeer = ({ item }: { item: any }) => (
    <TouchableOpacity 
      style={styles.peerRow} 
      onPress={() => router.push(`/chat/${item.id}`)}
    >
      <View style={[styles.avatar, item.isGroup && {backgroundColor: '#9B51E0'}]} />
      <View style={styles.peerInfo}>
        <Text style={styles.peerName}>{item.name}</Text>
        <Text style={styles.peerStatus}>{item.status}</Text>
      </View>
      <View style={styles.radioBtn} />
    </TouchableOpacity>
  );

  return (
    <View style={styles.container}>
      <View style={styles.header}>
        <TouchableOpacity onPress={() => router.back()} style={styles.backBtn}>
          <Text style={{color:'#FFF', fontSize: 20}}>←</Text>
        </TouchableOpacity>
        <Text style={styles.title}>New Message</Text>
      </View>

      <View style={styles.searchBar}>
        <Text style={{color: '#8895AA'}}>🔍</Text>
        <TextInput 
          style={styles.searchInput} 
          placeholder="Search peers..." 
          placeholderTextColor="#8895AA"
          value={search}
          onChangeText={setSearch}
        />
      </View>

      <FlatList
        showsVerticalScrollIndicator={false}
        data={[{ type: 'header', title: 'Frequently Connected' }, ...FREQUENT_PEERS, { type: 'header', title: 'All Peers' }, ...ALL_PEERS]}
        keyExtractor={(item, index) => item.id || `header-${index}`}
        renderItem={({ item }: any) => {
          if (item.type === 'header') {
             return <Text style={styles.sectionHeader}>{item.title}</Text>
          }
          return renderPeer({ item });
        }}
      />
      
      { /* Floating action button for creation */ }
      <View style={styles.fabContainer}>
         <TouchableOpacity style={styles.fabBtn}>
            <Text style={{color:'#000', fontSize: 18, fontWeight: 'bold'}}>→</Text>
         </TouchableOpacity>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#0B0F19' },
  header: { padding: 20, paddingTop: 60, flexDirection: 'row', alignItems: 'center' },
  backBtn: { marginRight: 15 },
  title: { fontSize: 20, fontWeight: 'bold', color: '#FFF' },
  searchBar: { flexDirection: 'row', alignItems: 'center', backgroundColor: '#1A2130', margin: 20, paddingHorizontal: 15, borderRadius: 12 },
  searchInput: { flex: 1, color: '#FFF', padding: 12, fontSize: 16 },
  sectionHeader: { color: '#8895AA', fontSize: 14, fontWeight: 'bold', paddingHorizontal: 20, marginTop: 10, marginBottom: 10 },
  peerRow: { flexDirection: 'row', alignItems: 'center', padding: 20, borderBottomWidth: 1, borderBottomColor: '#1A2130' },
  avatar: { width: 45, height: 45, borderRadius: 22.5, backgroundColor: '#4F5E7B', marginRight: 15 },
  peerInfo: { flex: 1 },
  peerName: { color: '#FFF', fontSize: 16, fontWeight: '600' },
  peerStatus: { color: '#8895AA', fontSize: 13, marginTop: 2 },
  radioBtn: { width: 20, height: 20, borderRadius: 10, borderWidth: 1, borderColor: '#4F5E7B' },
  fabContainer: { position: 'absolute', bottom: 30, right: 20 },
  fabBtn: { width: 60, height: 60, borderRadius: 30, backgroundColor: '#00FF66', justifyContent: 'center', alignItems: 'center' }
});
