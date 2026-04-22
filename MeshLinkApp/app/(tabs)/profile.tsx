import { View, Text, StyleSheet, TouchableOpacity } from 'react-native';
import { useRouter } from 'expo-router';

export default function ProfileScreen() {
  const router = useRouter();

  return (
    <View style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.headerTitle}>Profile</Text>
        <TouchableOpacity style={styles.editBtn}>
            <Text style={{color: '#FFF', fontSize: 18}}>✎</Text>
        </TouchableOpacity>
      </View>

      <View style={styles.profileCard}>
         <View style={styles.avatar}>
            <Text style={styles.cameraIcon}>📷</Text>
         </View>
         <Text style={styles.userName}>John Doe</Text>
         <View style={styles.idBox}>
            <Text style={styles.userId}>ML-7F3A-9021</Text>
            <Text style={{color: '#8895AA', marginLeft: 10}}>📋</Text>
         </View>
         <Text style={styles.status}>● Online</Text>
      </View>

      <View style={styles.menuList}>
         <TouchableOpacity style={styles.menuItem}>
            <Text style={styles.menuIcon}>QR</Text>
            <Text style={styles.menuText}>My QR Code</Text>
         </TouchableOpacity>

         <TouchableOpacity style={styles.menuItem}>
            <Text style={styles.menuIcon}>⚙️</Text>
            <Text style={styles.menuText}>Preferences</Text>
            <Text style={styles.chevron}>›</Text>
         </TouchableOpacity>

         <TouchableOpacity style={styles.menuItem}>
            <Text style={styles.menuIcon}>🔒</Text>
            <Text style={styles.menuText}>Security</Text>
            <Text style={styles.chevron}>›</Text>
         </TouchableOpacity>

         <TouchableOpacity style={styles.menuItem}>
            <Text style={styles.menuIcon}>ℹ️</Text>
            <Text style={styles.menuText}>About MeshLink</Text>
            <Text style={styles.chevron}>›</Text>
         </TouchableOpacity>
      </View>

      <TouchableOpacity style={styles.logoutBtn} onPress={() => router.replace('/')}>
         <Text style={styles.logoutText}>Log Out</Text>
      </TouchableOpacity>
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#0B0F19' },
  header: { padding: 20, paddingTop: 60, flexDirection: 'row', justifyContent: 'center', alignItems: 'center' },
  headerTitle: { fontSize: 20, fontWeight: 'bold', color: '#FFF' },
  editBtn: { position: 'absolute', right: 20, top: 60 },
  profileCard: { alignItems: 'center', marginVertical: 30 },
  avatar: { width: 100, height: 100, borderRadius: 50, backgroundColor: '#9B51E0', justifyContent: 'flex-end', alignItems: 'flex-end', padding: 5 },
  cameraIcon: { fontSize: 20 },
  userName: { color: '#FFF', fontSize: 22, fontWeight: 'bold', marginTop: 15 },
  idBox: { flexDirection: 'row', alignItems: 'center', marginTop: 5 },
  userId: { color: '#8895AA', fontSize: 14, fontFamily: 'monospace' },
  status: { color: '#00FF66', fontSize: 14, marginTop: 10 },
  menuList: { paddingHorizontal: 20 },
  menuItem: { flexDirection: 'row', alignItems: 'center', paddingVertical: 20, borderBottomWidth: 1, borderBottomColor: '#1A2130' },
  menuIcon: { width: 30, fontSize: 18, color: '#8895AA' },
  menuText: { flex: 1, color: '#FFF', fontSize: 16 },
  chevron: { color: '#8895AA', fontSize: 20 },
  logoutBtn: { marginTop: 40, alignItems: 'center' },
  logoutText: { color: '#EB5757', fontSize: 16, fontWeight: 'bold' }
});
