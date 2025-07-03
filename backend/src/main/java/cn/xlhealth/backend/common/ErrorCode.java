package cn.xlhealth.backend.common;

/**
 * 错误码常量类
 * 根据API规范定义的标准错误码
 */
public class ErrorCode {

  // 成功响应
  public static final Integer SUCCESS = 0;

  // 客户端错误 (10xxx)
  public static final Integer BAD_REQUEST = 10001; // 请求参数错误
  public static final Integer UNAUTHORIZED = 10002; // 未授权访问
  public static final Integer FORBIDDEN = 10003; // 权限不足
  public static final Integer NOT_FOUND = 10004; // 资源不存在
  public static final Integer CONFLICT = 10005; // 资源冲突

  // 服务器错误 (50xxx)
  public static final Integer INTERNAL_ERROR = 50001; // 服务器内部错误
  public static final Integer BAD_GATEWAY = 50002; // 外部服务错误

  // 通用错误
  public static final Integer ERROR = 99999; // 通用错误

  private ErrorCode() {
    // 私有构造函数，防止实例化
  }
}