// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: IpcMessageRequestProtobuf.proto

// Protobuf Java Version: 3.25.1
package com.lee.ipc.common.serialization.protobuf.request;

public final class IpcMessageRequestProtobuf {
  private IpcMessageRequestProtobuf() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_IpcMessageRequestProto_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_IpcMessageRequestProto_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_IpcMessageRequestProto_UserDataEntry_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_IpcMessageRequestProto_UserDataEntry_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\037IpcMessageRequestProtobuf.proto\"\364\001\n\026Ip" +
      "cMessageRequestProto\022\032\n\022service_unique_k" +
      "ey\030\001 \001(\t\022\027\n\017interface_class\030\002 \001(\t\022\023\n\013met" +
      "hod_name\030\003 \001(\t\022\027\n\017parameter_types\030\004 \003(\014\022" +
      "\014\n\004args\030\005 \003(\014\0228\n\tuser_data\030\006 \003(\0132%.IpcMe" +
      "ssageRequestProto.UserDataEntry\032/\n\rUserD" +
      "ataEntry\022\013\n\003key\030\001 \001(\t\022\r\n\005value\030\002 \001(\014:\0028\001" +
      "BP\n1com.lee.ipc.common.serialization.pro" +
      "tobuf.requestB\031IpcMessageRequestProtobuf" +
      "P\001b\006proto3"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        });
    internal_static_IpcMessageRequestProto_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_IpcMessageRequestProto_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_IpcMessageRequestProto_descriptor,
        new java.lang.String[] { "ServiceUniqueKey", "InterfaceClass", "MethodName", "ParameterTypes", "Args", "UserData", });
    internal_static_IpcMessageRequestProto_UserDataEntry_descriptor =
      internal_static_IpcMessageRequestProto_descriptor.getNestedTypes().get(0);
    internal_static_IpcMessageRequestProto_UserDataEntry_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_IpcMessageRequestProto_UserDataEntry_descriptor,
        new java.lang.String[] { "Key", "Value", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}
