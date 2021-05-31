package gobang;

import java.io.Serializable;
import java.util.Arrays;

/**
 * 棋盘模型
 */
public class GobangModel implements Serializable {
	static private GobangModel model; // 定义自身对象，单例模式可保持所有的GobangModel都是同一个对象
	static private byte[][] chessmanArray = new byte[15][15]; // 定义棋子数组
	public final static byte WHITE_CHESSMAN = 1;// 白棋的值
	public final static byte BLACK_CHESSMAN = -1;// 黑棋的值

	/**
	 * 获取本类实例的方法
	 *
	 * @return 自身对象
	 */
	public static GobangModel getInstance() {
		if (model == null) {// 如果model是null
			model = new GobangModel();// 则创建新对象
		}
		return model;
	}

	/**
	 * 棋盘模型的私有构造方法
	 */
	private GobangModel() {
		model = this;
	}

	/**
	 * 获取棋盘的棋子数组的方法
	 *
	 * @return - 代表棋子的数组
	 */
	public byte[][] getChessmanArray() {
		return chessmanArray; // 返回棋子数组
	}

	/**
	 * 载入棋子数组的方法
	 *
	 * @param chessmanArray
	 *            - 一个代表棋盘棋子的二维数组
	 */
	public void setChessmanArray(byte[][] chessmanArray) {
		// 设置棋盘数组方法开始执行
		this.chessmanArray = chessmanArray;// 将参数传来的棋盘数据作为本类的棋盘数据
	}

	/**
	 * 获取棋盘上棋子数组的拷贝
	 *
	 * @return - 棋子数组
	 */
	byte[][] getChessmanArrayCopy() {
		byte[][] newArray = new byte[15][15]; // 创建一个二维数组
		for (int i = 0; i < newArray.length; i++) {
			// 复制数组
			newArray[i] = Arrays.copyOf(chessmanArray[i], newArray[i].length);
		}
		return newArray;
	}
}
