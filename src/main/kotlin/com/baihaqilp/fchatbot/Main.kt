package com.baihaqilp.fchatbot

import spark.Spark.*

import com.clivern.racter.BotPlatform
import com.clivern.racter.receivers.webhook.*
import com.clivern.racter.senders.*
import com.clivern.racter.senders.templates.*


import java.util.HashMap
import java.io.IOException

object Main {
  @Throws(IOException::class)
  @JvmStatic fun main(args:Array<String>) {
    // Verify Token Route
    get("/", { request, response->
              val platform = BotPlatform("src/main/java/resources/config.properties")
              platform.getVerifyWebhook().setHubMode(if ((request.queryParams("hub.mode") != null)) request.queryParams("hub.mode") else "")
              platform.getVerifyWebhook().setHubVerifyToken(if ((request.queryParams("hub.verify_token") != null)) request.queryParams("hub.verify_token") else "")
              platform.getVerifyWebhook().setHubChallenge(if ((request.queryParams("hub.challenge") != null)) request.queryParams("hub.challenge") else "")
              if (platform.getVerifyWebhook().challenge())
              {
                platform.finish()
                response.status(200)
                return@get if ((request.queryParams("hub.challenge") != null)) request.queryParams("hub.challenge") else ""
              }
              platform.finish()
              response.status(403)
              "Verification token mismatch" })
    post("/", { request, response->
               val body = request.body()
               val platform = BotPlatform("src/main/java/resources/config.properties")
               platform.getBaseReceiver().set(body).parse()
               val messages = platform.getBaseReceiver().getMessages() as HashMap<String, MessageReceivedWebhook>
               for (message in messages.values)
               {
                 val user_id = if ((message.hasUserId())) message.getUserId() else ""
                 val page_id = if ((message.hasPageId())) message.getPageId() else ""
                 val message_id = if ((message.hasMessageId())) message.getMessageId() else ""
                 val message_text = if ((message.hasMessageText())) message.getMessageText() else ""
                 val quick_reply_payload = if ((message.hasQuickReplyPayload())) message.getQuickReplyPayload() else ""
                 val timestamp = (if ((message.hasTimestamp())) message.getTimestamp() else 0).toLong()
                 val attachments = if ((message.hasAttachment())) message.getAttachment() as HashMap<String, String> else HashMap<String, String>()
                 platform.getLogger().info("User ID#:" + user_id)
                 platform.getLogger().info("Page ID#:" + page_id)
                 platform.getLogger().info("Message ID#:" + message_id)
                 platform.getLogger().info("Message Text#:" + message_text)
                 platform.getLogger().info("Quick Reply Payload#:" + quick_reply_payload)
                 for (attachment in attachments.values)
                 {
                   platform.getLogger().info("Attachment#:" + attachment)
                 }
                 val text = message.getMessageText()
                 val message_tpl = platform.getBaseSender().getMessageTemplate()
                 val button_message_tpl = platform.getBaseSender().getButtonTemplate()
                 val list_message_tpl = platform.getBaseSender().getListTemplate()
                 val generic_message_tpl = platform.getBaseSender().getGenericTemplate()
                 val receipt_message_tpl = platform.getBaseSender().getReceiptTemplate()
                 if (text == "text")
                 {
                   message_tpl.setRecipientId(message.getUserId())
                   message_tpl.setMessageText("Hello World")
                   message_tpl.setNotificationType("REGULAR")
                   platform.getBaseSender().send(message_tpl)
                 }
                 else if (text == "image")
                 {
                   message_tpl.setRecipientId(message.getUserId())
                   message_tpl.setAttachment("image", "http://techslides.com/demos/samples/sample.jpg", false)
                   message_tpl.setNotificationType("SILENT_PUSH")
                   platform.getBaseSender().send(message_tpl)
                 }
                 else if (text == "file")
                 {
                   message_tpl.setRecipientId(message.getUserId())
                   message_tpl.setAttachment("file", "http://techslides.com/demos/samples/sample.pdf", false)
                   message_tpl.setNotificationType("NO_PUSH")
                   platform.getBaseSender().send(message_tpl)
                 }
                 else if (text == "video")
                 {
                   message_tpl.setRecipientId(message.getUserId())
                   message_tpl.setAttachment("video", "http://techslides.com/demos/samples/sample.mp4", false)
                   platform.getBaseSender().send(message_tpl)
                 }
                 else if (text == "audio")
                 {
                   message_tpl.setRecipientId(message.getUserId())
                   message_tpl.setAttachment("audio", "http://techslides.com/demos/samples/sample.mp3", false)
                   platform.getBaseSender().send(message_tpl)
                 }
                 return@post "ok"
               }
               "bla" })
  }
}