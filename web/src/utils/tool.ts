export class Tool {
  /**
   * 空校验 null或""都返回true
   */
  public static isEmpty (obj: any) {
    if ((typeof obj === 'string')) {
      return !obj || obj.replace(/\s+/g, "") === ""
    } else {
      return (!obj || JSON.stringify(obj) === "{}" || obj.length === 0);
    }
  }

  /**
   * 非空校验
   */
  public static isNotEmpty (obj: any) {
    return !this.isEmpty(obj);
  }

  /**
   * 对象复制
   * @param obj
   */
  public static copy (obj: object) {
    if (Tool.isNotEmpty(obj)) {
      return JSON.parse(JSON.stringify(obj));//JSON.parse() JSON 通常用于与服务端交换数据。 在接收服务器数据时一般是字符串。 我们可以使用 JSON.parse() 方法将数据转换为 JavaScript 对象 JSON.stringify()只能序列化可枚举属性,不可枚举的属性默认会被自动忽略

    }
  }

  /**
   * 使用递归将数组转为树形结构
   * 父ID属性为parent
   */
  public static array2Tree (array: any, parentId: number) {
    if (Tool.isEmpty(array)) {
      return [];
    }

    const result = [];
    for (let i = 0; i < array.length; i++) {
      const c = array[i];
      // console.log(Number(c.parent), Number(parentId));
      if (Number(c.parent) === Number(parentId)) {
        result.push(c);

        // 递归查看当前节点对应的子节点
        const children = Tool.array2Tree(array, c.id);
        if (Tool.isNotEmpty(children)) {
          c.children = children;
        }
      }
    }
    return result;
  }

  /**
   * 随机生成[len]长度的[radix]进制数
   * @param len
   * @param radix 默认62
   * @returns {string}
   */
  // public static uuid (len: number, radix = 62) {
  //   const chars = '0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz'.split('');
  //   const uuid = [];
  //   radix = radix || chars.length;
  //
  //   for (let i = 0; i < len; i++) {
  //     uuid[i] = chars[0 | Math.random() * radix];
  //   }
  //
  //   return uuid.join('');
  // }
}
