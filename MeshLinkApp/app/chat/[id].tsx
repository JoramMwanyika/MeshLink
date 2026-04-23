import { View, Text, StyleSheet, TextInput, FlatList, KeyboardAvoidingView, Platform, TouchableOpacity } from 'react-native';
import { useLocalSearchParams, useRouter } from 'expo-router';
import { useState, useEffect } from 'react';
import { MeshEngine } from '../../core/mesh/MeshEngine';
import { DBManager } from '../../core/database/dbManager';

export default function ChatScreen() {
  const { id } = useLocalSearchParams();
  const router = useRouter();
  const [inputText, setInputText] = useState('');
  const [messages, setMessages] = useState<any[]>([]);

  useEffect(() => {
    const loadMessages = async () => {
        const msgs = await DBManager.getInstance().getMessagesForChat(id as string);
        setMessages(msgs.map((m: any) => ({
            id: m.id,
            text: m.content,
            sender: m.sender_id === 'SELF' ? 'me' : 'them',
            time: new Date(m.timestamp).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
            status: m.status
        })));
    };

    loadMessages();

    // Subscribe to new messages
    const unsubscribe = MeshEngine.getInstance().subscribeToMessages((msg) => {
        if (msg.sender_id === id || msg.receiver_id === id) {
            loadMessages();
        }
    });

    return unsubscribe;
  }, [id]);

  const handleSend = async () => {
    if (!inputText.trim()) return;

    try {
        const engine = MeshEngine.getInstance();
        await engine.sendDirect(id as string, inputText);
        await loadMessages();
        setInputText('');
    } catch (e) {
        console.error("Failed to send message over mesh", e);
    }
  };

  return (
    <KeyboardAvoidingView style={styles.container} behavior={Platform.OS === 'ios' ? 'padding' : undefined}>
      <View style={styles.header}>
        <TouchableOpacity onPress={() => router.back()} style={styles.backBtn}>
          <Text style={{color:'#FFF', fontSize: 20}}>←</Text>
        </TouchableOpacity>
        <View style={styles.headerInfo}>
            <Text style={styles.userName}>Sarah (ID: {id})</Text>
            <Text style={styles.userStatus}>Online (2m away)</Text>
        </View>
      </View>

      <FlatList
        data={messages}
        keyExtractor={i => i.id}
        style={styles.chatArea}
        contentContainerStyle={{ padding: 20, gap: 15 }}
        renderItem={({ item }) => {
          const isMe = item.sender === 'me';
          return (
            <View style={[styles.messageBubble, isMe ? styles.bubbleMe : styles.bubbleThem]}>
              <Text style={[styles.messageText, isMe && {color: '#000'}]}>{item.text}</Text>
              <View style={styles.metaRow}>
                <Text style={[styles.time, isMe && {color: '#333'}]}>{item.time}</Text>
                {isMe && <Text style={styles.ticks}>{item.status === 'delivered' ? '✓✓' : '✓'}</Text>}
              </View>
            </View>
          );
        }}
      />

      <View style={styles.inputArea}>
        <TextInput 
          style={styles.input} 
          placeholder="Type a message..." 
          placeholderTextColor="#8895AA"
          value={inputText}
          onChangeText={setInputText}
        />
        <TouchableOpacity style={styles.sendBtn} onPress={handleSend}>
           <Text style={styles.sendIcon}>^</Text>
        </TouchableOpacity>
      </View>
    </KeyboardAvoidingView>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#0B0F19' },
  header: { padding: 20, paddingTop: 60, flexDirection: 'row', alignItems: 'center', borderBottomWidth: 1, borderBottomColor: '#1A2130' },
  backBtn: { marginRight: 15, padding: 5 },
  headerInfo: { flex: 1 },
  userName: { color: '#FFF', fontSize: 18, fontWeight: 'bold' },
  userStatus: { color: '#00FF66', fontSize: 13 },
  chatArea: { flex: 1 },
  messageBubble: { maxWidth: '80%', padding: 15, borderRadius: 16 },
  bubbleThem: { backgroundColor: '#1A2130', alignSelf: 'flex-start', borderBottomLeftRadius: 4 },
  bubbleMe: { backgroundColor: '#00FF66', alignSelf: 'flex-end', borderBottomRightRadius: 4 },
  messageText: { color: '#FFF', fontSize: 16, marginBottom: 5 },
  metaRow: { flexDirection: 'row', justifyContent: 'flex-end', alignItems: 'center', gap: 5 },
  time: { color: '#8895AA', fontSize: 11 },
  ticks: { color: '#333', fontSize: 12 },
  inputArea: { padding: 15, paddingBottom: 30, flexDirection: 'row', alignItems: 'center', gap: 10, backgroundColor: '#0B0F19' },
  input: { flex: 1, backgroundColor: '#1A2130', padding: 15, borderRadius: 25, color: '#FFF', fontSize: 16 },
  sendBtn: { width: 45, height: 45, backgroundColor: '#00FF66', borderRadius: 25, justifyContent: 'center', alignItems: 'center' },
  sendIcon: { fontSize: 24, fontWeight: 'bold' }
});
