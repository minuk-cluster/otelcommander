package dev.minuk.otelcommander.domain.services

import dev.minuk.otelcommander.domain.models.agent.Agent
import dev.minuk.otelcommander.domain.port.`in`.agent.GetAgentInternalUsecase
import dev.minuk.otelcommander.domain.port.`in`.agent.UpsertAgentInternalUsecase
import dev.minuk.otelcommander.util.Logger
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class AgentService : GetAgentInternalUsecase, UpsertAgentInternalUsecase {
        companion object {
            val log by Logger()
        }

    override suspend fun getAgent(instanceUid: UUID): Agent? {
        return null
    }

    override suspend fun upsertAgent(agent: Agent): Boolean {
        // TODO: Implement
        return true
    }
}