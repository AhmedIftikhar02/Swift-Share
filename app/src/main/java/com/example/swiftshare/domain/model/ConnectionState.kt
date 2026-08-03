package com.example.swiftshare.domain.model

/**
 * Single source of truth for the app's overall Nearby connection lifecycle (PRD 7.6),
 * exposed as a StateFlow from `NearbyRepositoryImpl` (Section 5.2 of this guide) and
 * observable by any screen (Discovery, Transfer Hub, pairing screens) without duplicated
 * local state.
 */
enum class ConnectionState { IDLE, DISCOVERING, CONNECTING, CONNECTED, DISCONNECTING }