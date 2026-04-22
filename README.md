# MeshLink

**MeshLink** is a local-first, decentralized messaging application that creates a peer-to-peer (P2P) mesh network using Bluetooth and Wi-Fi Direct. It is designed to function entirely offline without reliance on centralized cloud servers or internet infrastructure.

> **Status Update:** The architecture has been pivoted from a React Native bridge prototype directly into a **100% Native Android (Kotlin)** application for unparalleled access to the Android BLE hardware stack, stability, and Hackathon-grade performance.

## Architecture & Data Flow

MeshLink operates as a distributed system using event-based replication. There is no shared central server database. Each device runs its own isolated `androidx.room` instance.

**Core Data Models:**
- `Devices`: (device_id, name, last_seen)
- `Messages`: (message_id, sender_id, receiver_id, group_id, content, timestamp, ttl, status)
- `Groups`: (group_id, group_name, created_by)

**The Relay Loop:**
```mermaid
graph LR
    A[CREATE] --> B[STORE Locally]
    B --> C[SEND via Broadcast]
    C --> D[Peer Receives]
    D --> E{Is Recipient?}
    E -- Yes --> F[DELIVER & STORE]
    E -- No --> G{Check TTL & Duplicates}
    G -- Valid --> H[RELAY (Re-broadcast)]
    H --> B
```

## Getting Started

Because MeshLink is a pure Native Android application, you will need **Android Studio**.

### Setup Instructions
1. Open **Android Studio**.
2. Click **New Project** -> **Empty Compose Activity**.
3. Select this directory as the project destination to safely populate the latest Gradle / OS builds seamlessly.
4. Plug in your physical Android devices (USB Debugging Enabled).
5. Press the **Play** button in Android Studio to push the APK locally.

## Hackathon Talking Points
If presenting, emphasize the following to judges:
- *"We don’t share databases across devices. Each device maintains its own local database, and synchronization happens through message passing and replication across the mesh network."*
- Effectively operates as a Delay-Tolerant Network (DTN).
- Each phone acts dynamically as a client and a relay node concurrently.
