package com.lee.ipc.common.constant;

/**
 * 消息类型枚举
 * @author yanhuai lee
 */
public enum MessageType {

  HEARTBEAT(0),

  NORMAL(1);

  final int messageTypeCode;

  MessageType(int messageTypeCode) {
    this.messageTypeCode = messageTypeCode;
  }

 public int getMessageTypeCode() {
  return messageTypeCode;
 }
}
