package dev.minuk.otelcommander.adapter.`in`.http.v1.opamp

import com.github.f4b6a3.ulid.Ulid
import com.google.protobuf.ByteString
import opamp.proto.Opamp.AgentToServer
import opamp.proto.Opamp.ServerToAgent
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.http.MediaType
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.BodyInserters
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

@WebFluxTest(OpampController::class)
@ExtendWith(SpringExtension::class)
class OpampControllerTest {

    @Autowired
    lateinit var webClient: WebTestClient

    @Test
    fun `exchange When OpampAgentToServer_Requested Then Response_OpampServerToAgent_With_InstanceUid`() {
        val instanceUid = Ulid.fast()

        val request = AgentToServer.newBuilder()
                .setInstanceUid(ByteString.copyFrom(instanceUid.toBytes()))
                .build()
        println(request.toByteArray())
        println(request.toByteArray().compressGzip())
        println(request.toByteArray().compressGzip().decompressGzip())

        // when
        webClient.post()
            .uri("/api/v1/opamp")
            .contentType(MediaType.APPLICATION_PROTOBUF)
            .header("accept-encoding", "gzip")
            .body(BodyInserters.fromValue(request.toByteArray().compressGzip()))
            .exchange()
            .expectStatus().isOk // when
            .expectBody()
            .returnResult().responseBody.let {
                val serverToAgent = ServerToAgent.parseFrom(it)
                val actualInstanceUid = Ulid.from(serverToAgent.instanceUid.toByteArray())
                assertEquals(instanceUid, actualInstanceUid)
            }
    }

    fun ByteArray.compressGzip(): ByteArray {
        val bos = ByteArrayOutputStream()
        val gzipStream = GZIPOutputStream(bos)
        gzipStream.write(this)
        gzipStream.close()
        return bos.toByteArray()
    }

    fun ByteArray.decompressGzip(): ByteArray {
        val bais = ByteArrayInputStream(this)
        return GZIPInputStream(bais).readAllBytes()
    }
}
