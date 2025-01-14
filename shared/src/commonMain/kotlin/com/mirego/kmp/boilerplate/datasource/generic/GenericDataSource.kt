package com.mirego.kmp.boilerplate.datasource.generic

import com.mirego.kmp.boilerplate.datasource.DataSourceUtils
import com.mirego.trikot.datasources.flow.BaseExpiringExecutableFlowDataSource
import com.mirego.trikot.datasources.flow.ExpiringFlowDataSourceRequest
import com.mirego.trikot.datasources.flow.FlowDataSourceRequest
import kotlin.time.Duration.Companion.minutes
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json

open class GenericDataSource<T>(
    json: Json,
    dataSerializer: KSerializer<T>,
    diskCachePath: String?
) : BaseExpiringExecutableFlowDataSource<GenericDataSourceRequest<T>, T>(
    diskCachePath?.let { GenericDiskDataSource(json, dataSerializer, diskCachePath) }
) {
    override suspend fun internalRead(request: GenericDataSourceRequest<T>) = DataSourceUtils.buildExpiringValue(request.block(), request)
}

data class GenericDataSourceRequest<T>(
    override val cacheableId: String,
    override val expiredInMilliseconds: Long = 1.minutes.inWholeMilliseconds,
    override val requestType: FlowDataSourceRequest.Type = FlowDataSourceRequest.Type.USE_CACHE,
    val block: suspend () -> T
) : ExpiringFlowDataSourceRequest
