export const DATABASE_NAME = 'meshlink.db';

export const INIT_QUERIES = [
  // Devices Table (renamed from peers to match Hackathon spec)
  `CREATE TABLE IF NOT EXISTS devices (
    device_id TEXT PRIMARY KEY,
    name TEXT NOT NULL,
    last_seen INTEGER NOT NULL
  );`,

  // Groups Table 
  `CREATE TABLE IF NOT EXISTS groups (
    group_id TEXT PRIMARY KEY,
    group_name TEXT NOT NULL,
    created_by TEXT NOT NULL
  );`,

  // Group Members Table 
  `CREATE TABLE IF NOT EXISTS group_members (
    group_id TEXT NOT NULL,
    device_id TEXT NOT NULL,
    PRIMARY KEY (group_id, device_id),
    FOREIGN KEY (group_id) REFERENCES groups (group_id) ON DELETE CASCADE,
    FOREIGN KEY (device_id) REFERENCES devices (device_id) ON DELETE CASCADE
  );`,

  // Messages Table
  `CREATE TABLE IF NOT EXISTS messages (
    id TEXT PRIMARY KEY,
    sender_id TEXT NOT NULL,
    receiver_id TEXT, 
    group_id TEXT, 
    content TEXT NOT NULL, 
    timestamp INTEGER NOT NULL,
    ttl INTEGER NOT NULL,
    status TEXT NOT NULL 
  );`
];
