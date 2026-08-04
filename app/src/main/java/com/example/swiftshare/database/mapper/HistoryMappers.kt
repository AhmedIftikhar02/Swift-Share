package com.example.swiftshare.database.mapper

import com.example.swiftshare.database.dao.SessionWithFiles
import com.example.swiftshare.database.entity.FileTransferEntity
import com.example.swiftshare.database.entity.TransferSessionEntity
import com.example.swiftshare.domain.model.DeviceAvailability
import com.example.swiftshare.domain.model.DeviceModel
import com.example.swiftshare.domain.model.DeviceType
import com.example.swiftshare.domain.model.FileTransferModel
import com.example.swiftshare.domain.model.FileTransferStatus
import com.example.swiftshare.domain.model.SessionStatus
import com.example.swiftshare.domain.model.TransferDirection
import com.example.swiftshare.domain.model.TransferSessionModel

fun SessionWithFiles.toDomain(): TransferSessionModel = TransferSessionModel(
    sessionId = session.sessionId,
    device = DeviceModel(
        endpointId = session.deviceEndpointId,
        displayName = session.deviceName,
        deviceType = DeviceType.UNKNOWN,
        availability = DeviceAvailability.UNAVAILABLE
    ),
    direction = TransferDirection.valueOf(session.direction),
    startedAt = session.startedAt,
    endedAt = session.endedAt,
    status = SessionStatus.valueOf(session.status),
    files = files.mapNotNull { runCatching { it.toDomain() }.getOrNull() }
)

fun FileTransferEntity.toDomain(): FileTransferModel = FileTransferModel(
    fileTransferId = fileTransferId,
    sessionId = sessionId,
    fileName = fileName,
    mimeType = mimeType,
    totalBytes = totalBytes,
    transferredBytes = transferredBytes,
    uri = uri,
    status = FileTransferStatus.valueOf(status),
    checksum = checksum,
    errorCode = errorCode
)

fun TransferSessionModel.toEntity(): TransferSessionEntity = TransferSessionEntity(
    sessionId = sessionId,
    deviceEndpointId = device.endpointId,
    deviceName = device.displayName,
    direction = direction.name,
    startedAt = startedAt,
    endedAt = endedAt,
    status = status.name
)

fun FileTransferModel.toEntity(sessionId: String): FileTransferEntity = FileTransferEntity(
    fileTransferId = fileTransferId,
    sessionId = sessionId,
    fileName = fileName,
    mimeType = mimeType,
    totalBytes = totalBytes,
    transferredBytes = transferredBytes,
    uri = uri,
    status = status.name,
    checksum = checksum,
    errorCode = errorCode
)