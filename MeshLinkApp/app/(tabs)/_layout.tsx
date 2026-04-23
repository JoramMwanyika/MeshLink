import { Tabs } from 'expo-router';

export default function TabLayout() {
  return (
    <Tabs screenOptions={{
      headerShown: false,
      tabBarStyle: { backgroundColor: '#050B14', borderTopWidth: 0, height: 80, paddingBottom: 25, elevation: 0 },
      tabBarActiveTintColor: '#00FF66',
      tabBarInactiveTintColor: '#4F5E7B',
    }}>
      <Tabs.Screen name="index" options={{ title: 'Chats' }} />
      <Tabs.Screen name="peers" options={{ title: 'Peers' }} />
      <Tabs.Screen name="map" options={{ title: 'Map' }} />
      <Tabs.Screen name="inbox" options={{ title: 'Inbox' }} />
      <Tabs.Screen name="profile" options={{ title: 'Profile' }} />
      <Tabs.Screen name="simulate" options={{ title: 'Sim' }} />
    </Tabs>
  );
}
