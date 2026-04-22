import { View, Text, StyleSheet, FlatList, TouchableOpacity } from 'react-native';
import { useRouter } from 'expo-router';

const MOCK_CHATS = [
  { id: '1', name: 'Sarah', msg: 'Hey! Are you coming to the...', time: '10:24 AM', unread: 2 },
  { id: '2', name: 'Team Alpha', msg: 'Meeting point updated.', time: '09:11 AM', unread: 3 },
  { id: '3', name: 'Mike', msg: 'Got the supplies.', time: 'Yesterday', unread: 0 },
];

export default function ChatsScreen() {
  const router = useRouter();

  return (
    <View style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.headerTitle}>MeshLink</Text>
      </View>

      <View style={styles.statusWidget}>
        <Text style={styles.statusText}>You are connected</Text>
        <Text style={styles.statusSub}>3 peers around you</Text>
      </View>

      <View style={styles.listHeader}>
        <Text style={styles.listTitle}>Recent Chats</Text>
        <TouchableOpacity style={styles.addButton}>
          <Text style={styles.addIcon}>+</Text>
        </TouchableOpacity>
      </View>

      <FlatList
        data={MOCK_CHATS}
        keyExtractor={(item) => item.id}
        renderItem={({ item }) => (
          <TouchableOpacity 
            style={styles.chatRow}
            onPress={() => router.push(`/chat/${item.id}`)}
          >
            <View style={styles.avatar} />
            <View style={styles.chatContent}>
              <Text style={styles.chatName}>{item.name}</Text>
              <Text style={styles.chatMsg} numberOfLines={1}>{item.msg}</Text>
            </View>
            <View style={styles.chatMeta}>
              <Text style={styles.chatTime}>{item.time}</Text>
              {item.unread > 0 && (
                <View style={styles.unreadBadge}>
                  <Text style={styles.unreadText}>{item.unread}</Text>
                </View>
              )}
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
  statusWidget: { margin: 20, padding: 20, backgroundColor: '#131B2A', borderRadius: 16, borderWidth: 1, borderColor: '#00FF6633' },
  statusText: { color: '#00FF66', fontWeight: 'bold', fontSize: 16 },
  statusSub: { color: '#8895AA', marginTop: 4 },
  listHeader: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center', paddingHorizontal: 20, marginBottom: 10 },
  listTitle: { color: '#FFF', fontSize: 18, fontWeight: '600' },
  addButton: { width: 30, height: 30, borderRadius: 15, backgroundColor: '#00FF66', justifyContent: 'center', alignItems: 'center' },
  addIcon: { color: '#000', fontWeight: 'bold', fontSize: 20, lineHeight: 22 },
  chatRow: { flexDirection: 'row', padding: 20, alignItems: 'center', borderBottomWidth: 1, borderBottomColor: '#131B2A' },
  avatar: { width: 50, height: 50, borderRadius: 25, backgroundColor: '#4F5E7B', marginRight: 15 },
  chatContent: { flex: 1 },
  chatName: { color: '#FFF', fontSize: 16, fontWeight: 'bold', marginBottom: 5 },
  chatMsg: { color: '#8895AA', fontSize: 14 },
  chatMeta: { alignItems: 'flex-end' },
  chatTime: { color: '#8895AA', fontSize: 12, marginBottom: 5 },
  unreadBadge: { backgroundColor: '#00FF66', paddingHorizontal: 6, paddingVertical: 2, borderRadius: 10 },
  unreadText: { color: '#000', fontSize: 10, fontWeight: 'bold' }
});
