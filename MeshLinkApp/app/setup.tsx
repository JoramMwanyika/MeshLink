import { View, Text, TextInput, StyleSheet, TouchableOpacity } from 'react-native';
import { useRouter } from 'expo-router';
import { useState } from 'react';
import * as Crypto from 'expo-crypto';

export default function SetupScreen() {
  const router = useRouter();
  const [name, setName] = useState('');
  // Crypto requires expo-crypto installed
  const autoId = `ML-` + (Crypto.randomUUID ? Crypto.randomUUID().substring(0,8).toUpperCase() : "TEMP123");

  const handleContinue = () => {
    if (name.trim()) {
      router.push('/(tabs)');
    }
  };

  return (
    <View style={styles.container}>
      <Text style={styles.header}>Create Your Identity</Text>
      <Text style={styles.subtext}>This helps others recognize you in the mesh network.</Text>

      <View style={styles.avatarPlaceholder}>
        <View style={styles.cameraIcon} />
      </View>

      <TextInput 
        style={styles.input} 
        placeholder="John Doe" 
        placeholderTextColor="#555"
        value={name}
        onChangeText={setName}
      />

      <View style={styles.idBox}>
         <Text style={styles.idBoxText}>Auto ID: {autoId}</Text>
      </View>

      <Text style={styles.colorLabel}>Pick a color</Text>
      <View style={styles.colorPicker}>
        <View style={[styles.colorBubble, {backgroundColor: '#00FF66', borderWidth: 2, borderColor: '#FFF'}]} />
        <View style={[styles.colorBubble, {backgroundColor: '#9B51E0'}]} />
        <View style={[styles.colorBubble, {backgroundColor: '#2D9CDB'}]} />
        <View style={[styles.colorBubble, {backgroundColor: '#F2994A'}]} />
        <View style={[styles.colorBubble, {backgroundColor: '#EB5757'}]} />
      </View>

      <View style={{flex: 1}} />

      <TouchableOpacity 
        style={[styles.primaryButton, !name && {opacity: 0.5}]} 
        onPress={handleContinue}
        disabled={!name}
      >
        <Text style={styles.primaryButtonText}>Continue</Text>
      </TouchableOpacity>
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#0B0F19', padding: 20 },
  header: { fontSize: 24, fontWeight: 'bold', color: '#FFF', textAlign: 'center', marginTop: 40 },
  subtext: { color: '#888', textAlign: 'center', marginTop: 10, paddingHorizontal: 20, marginBottom: 40 },
  avatarPlaceholder: { width: 100, height: 100, borderRadius: 50, backgroundColor: '#00FF66', alignSelf: 'center', marginBottom: 40, justifyContent: 'flex-end', alignItems: 'flex-end' },
  cameraIcon: { width: 30, height: 30, backgroundColor: '#1C2536', borderRadius: 15, margin: 5 },
  input: { backgroundColor: '#1C2536', color: '#FFF', padding: 15, borderRadius: 12, fontSize: 16, marginBottom: 15 },
  idBox: { backgroundColor: '#1C2536', padding: 15, borderRadius: 12, flexDirection: 'row', justifyContent: 'space-between', marginBottom: 30 },
  idBoxText: { color: '#FFF', fontWeight: 'bold' },
  colorLabel: { color: '#FFF', marginBottom: 15 },
  colorPicker: { flexDirection: 'row', justifyContent: 'space-between', paddingHorizontal: 10 },
  colorBubble: { width: 40, height: 40, borderRadius: 20 },
  primaryButton: { backgroundColor: '#00FF66', width: '100%', padding: 16, borderRadius: 12, alignItems: 'center', marginBottom: 20 },
  primaryButtonText: { color: '#000', fontWeight: 'bold', fontSize: 16 },
});
