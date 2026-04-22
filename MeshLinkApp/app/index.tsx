import { View, Text, StyleSheet, TouchableOpacity } from 'react-native';
import { useRouter } from 'expo-router';

export default function SplashScreen() {
  const router = useRouter();
  return (
    <View style={styles.container}>
      <View style={styles.logoContainer}>
        <View style={styles.mockLogo} />
        <Text style={styles.title}>MeshLink</Text>
        <Text style={styles.tagline}>Offline. Private. Connected.</Text>
      </View>

      <View style={styles.bottomSection}>
        <Text style={styles.description}>
          Communicate securely with people around you, even without the internet.
        </Text>
        <TouchableOpacity style={styles.primaryButton} onPress={() => router.push('/setup')}>
          <Text style={styles.primaryButtonText}>Get Started</Text>
        </TouchableOpacity>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#0B0F19', padding: 20, justifyContent: 'space-between' },
  logoContainer: { flex: 1, justifyContent: 'center', alignItems: 'center' },
  mockLogo: { width: 100, height: 100, borderColor: '#00FF66', borderWidth: 2, borderRadius: 50, marginBottom: 20 },
  title: { fontSize: 32, fontWeight: 'bold', color: '#FFFFFF', marginBottom: 10 },
  tagline: { fontSize: 16, color: '#00FF66' },
  bottomSection: { paddingBottom: 40, alignItems: 'center' },
  description: { textAlign: 'center', color: '#A0AABF', fontSize: 14, marginBottom: 30, paddingHorizontal: 20 },
  primaryButton: { backgroundColor: '#00FF66', width: '100%', padding: 16, borderRadius: 12, alignItems: 'center', marginBottom: 15 },
  primaryButtonText: { color: '#000000', fontWeight: 'bold', fontSize: 16 }
});
