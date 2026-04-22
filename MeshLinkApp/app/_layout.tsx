import { Stack } from 'expo-router';
import { useEffect, useState } from 'react';
import { MeshEngine } from '../core/mesh/MeshEngine';
import { View, ActivityIndicator } from 'react-native';

export default function RootLayout() {
  const [isInitializing, setIsInitializing] = useState(true);

  useEffect(() => {
    async function setupBackend() {
      try {
        const engine = MeshEngine.getInstance();
        await engine.init(); 
        console.log("MeshEngine Background Initialized.");
      } catch (e) {
        console.warn("Init non-blocking issue:", e);
      } finally {
        setIsInitializing(false);
      }
    }
    setupBackend();
  }, []);

  if (isInitializing) {
    return (
      <View style={{ flex: 1, backgroundColor: '#0B0F19', justifyContent: 'center', alignItems: 'center' }}>
        <ActivityIndicator size="large" color="#00FF66" />
      </View>
    );
  }

  return (
    <Stack screenOptions={{ headerShown: false, contentStyle: { backgroundColor: '#0B0F19' } }}>
      <Stack.Screen name="index" />
      <Stack.Screen name="setup" />
      <Stack.Screen name="(tabs)" />
      <Stack.Screen name="chat/[id]" />
    </Stack>
  );
}
