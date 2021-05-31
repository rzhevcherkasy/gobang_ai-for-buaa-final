package gobang;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.net.URL;
import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;

import javax.swing.ImageIcon;

/**
 * 棋盘面板
 *
 */
public class GobangPanel extends javax.swing.JPanel {
	private Image backImg;// 背景图片
	private Image white_chessman_img;// 白棋图片
	private Image black_chessman_img;// 黑棋图片
	private Image rightTop_img;// 星星图片
	int chessWidth, chessHeight; // 棋子宽度与高度
	Dimension size; // 棋盘面板的大小
	private boolean start = false; // 游戏开始
	private Object[] oldRec;// 棋盘记录
	Deque<byte[][]> chessQueue = new LinkedList(); // 游戏棋局的队列记录
	private boolean turn = false; // 是否到自己走棋
	private boolean towardsWin; // 对方胜利
	private boolean win; // 胜利
	private boolean draw; // 和棋
	private ChessPanel chessPanel;// 下棋面板
	public byte myColor = -2;// 我使用的棋子颜色
	private GobangModel gobangModel1;// 棋子模型类

	/**
	 * 棋盘面板的构造方法
	 */
	public GobangPanel() {
		URL white_url = getClass().getResource("/res/whiteChessman.png");
		URL black_url = getClass().getResource("/res/blackChessman.png");
		URL rightTop_url = getClass().getResource("/res/rightTop.gif");
		white_chessman_img = new ImageIcon(white_url).getImage(); // 初始化白旗图片
		black_chessman_img = new ImageIcon(black_url).getImage(); // 初始化黑旗图片
		rightTop_img = new ImageIcon(rightTop_url).getImage();// 初始化连成线的棋子上的星图
		size = new Dimension(getWidth(), getHeight());// 定义组件宽高
		setPreferredSize(size);// 设置此组件的大小
		initComponents();// 面板初始化
	}

	/**
	 * 设制游戏为未开始状态
	 *
	 * @param start
	 */
	public void setStart(boolean start) {
		chessQueue.clear();// 清空游戏队列中所有数据
		this.start = start;
		if (chessPanel == null) {// 棋盘面板是空的
			chessPanel = (ChessPanel) getParent();// 获取上层组件对象
		}
		repaint();// 重绘组件
	}

	/**
	 * 重写父类的paint方法，绘制自己的组件界面
	 */
	@Override
	public void paint(Graphics g1) {
		Graphics2D g = (Graphics2D) g1;// 使用新绘图类
		super.paint(g); // 调用父类的绘图方法
		if (chessPanel != null) {
			chessPanel.setTurn(turn);
		}
		Composite composite = g.getComposite(); // 备份合成模式
		drawPanel(g); // 调用绘制棋盘的方法
		g.translate(4, 4);// 将4,4位置设为坐标原点
		size = new Dimension(getWidth(), getHeight());// 设置棋盘面板的大小
		chessWidth = size.width / 15; // 初始化棋子宽（除15个棋子）
		chessHeight = size.height / 15; // 初始化棋子高
		byte[][] chessmanArray = gobangModel1.getChessmanArrayCopy();// 获取棋盘数据
		for (int i = 0; i < chessmanArray.length; i++) {// 双for循环遍历棋盘数据模型
			for (int j = 0; j < chessmanArray[i].length; j++) {
				byte chessman = chessmanArray[i][j];
				int x = i * chessWidth;// 获取此处棋子左上角的横坐标
				int y = j * chessHeight;// 获取此处棋子左上角的纵坐标
				if (chessman != 0)// 如果此处有棋子
					if (chessman == GobangModel.WHITE_CHESSMAN) { // 如果是白子
						// 绘制白子图片，在指定坐标，指定宽高，绘于棋盘上
						g.drawImage(white_chessman_img, x, y, chessWidth,
								chessHeight, this);
					} else if (chessman == GobangModel.BLACK_CHESSMAN) {// 如果是黑子
						g.drawImage(black_chessman_img, x, y, chessWidth,
								chessHeight, this);// 绘制黑子
					} else if (chessman == (byte) (GobangModel.WHITE_CHESSMAN ^ 8)) {// 如果是导致胜利的连线白旗
						g.drawImage(white_chessman_img, x, y, chessWidth,
								chessHeight, this);// 绘制白子
						g.drawImage(rightTop_img, x, y, chessWidth,
								chessHeight, this);// 绘制星星
					} else if (chessman == (byte) (GobangModel.BLACK_CHESSMAN ^ 8)) {// 绘制导致胜利的连线黑旗
						g.drawImage(black_chessman_img, x, y, chessWidth,
								chessHeight, this);// 绘制黑子
						g.drawImage(rightTop_img, x, y, chessWidth,
								chessHeight, this);// 绘制星星
					}
			}
		}
		if (!isStart()) { // 如果游戏不处于开始状态
			// 如果处于对方胜利或者我自己胜利或者和棋状态，绘制棋盘提示信息
			if (towardsWin || win || draw) {
				g.setComposite(AlphaComposite.SrcOver.derive(0.7f)); // 透明的合成规则，设置70%
				String mess = "对方胜利"; // 定义提示信息
				g.setColor(Color.RED); // 设置前景色为红色
				if (win) { // 如果是自己胜利
					mess = "你胜利了"; // 设置胜利提示信息
					g.setColor(new Color(0x007700)); // 设置绿色前景色
				} else if (draw) { // 如果是和棋状态
					mess = "此战平局"; // 定义和棋提示信息
					g.setColor(Color.YELLOW); // 设置和棋信息使用黄色提示
				}
				// 设置提示文本的字体为隶书、粗斜体、大小72
				Font font = new Font("隶书", Font.ITALIC | Font.BOLD, 72);
				g.setFont(font);// 载入此字体
				// 获取字体渲染上下文对象
				FontRenderContext context = g.getFontRenderContext();
				// 计算提示信息的文本所占用的像素空间
				Rectangle2D stringBounds = font.getStringBounds(mess, context);
				double fontWidth = stringBounds.getWidth(); // 获取提示文本的宽度
				g.drawString(mess, (int) ((getWidth() - fontWidth) / 2),
						getHeight() / 2); // 居中绘制提示信息
				g.setComposite(composite); // 恢复原有合成规则
			} else { // 如果当前处于其他未开始游戏的状态
				String mess = "等待开始…"; // 定义等他提示信息
				Font font = new Font("隶书", Font.ITALIC | Font.BOLD, 48);
				g.setFont(font); // 设置48号隶书字体
				// 获取字体渲染上下文对象
				FontRenderContext context = g.getFontRenderContext();
				// 计算提示信息的文本所占用的像素空间
				Rectangle2D stringBounds = font.getStringBounds(mess, context);
				double fontWidth = stringBounds.getWidth(); // 获取提示文本的宽度
				g.drawString(mess, (int) ((getWidth() - fontWidth) / 2),
						getHeight() / 2); // 居中绘制提示文本
			}
		}
	}

	/**
	 * 绘制棋盘的方法
	 *
	 * @param g
	 *            - 绘图对象
	 */
	private void drawPanel(Graphics2D g) {
		Composite composite = g.getComposite(); // 备份合成规则
		Color color = g.getColor(); // 备份前景颜色
		g.setComposite(AlphaComposite.SrcOver.derive(0.6f));// 设置透明合成
		g.setColor(new Color(0xAABBAA)); // 设置前景白色
		g.fill3DRect(0, 0, getWidth(), getHeight(), true); // 绘制半透明的矩形
		g.setComposite(composite); // 恢复合成规则
		g.setColor(color); // 恢复原来前景色
		int w = getWidth(); // 棋盘宽度
		int h = getHeight(); // 棋盘高度
		int chessW = w / 15, chessH = h / 15; // 棋子宽度和高度
		int left = chessW / 2 + (w % 15) / 2; // 棋盘左边界
		int right = left + chessW * 14; // 棋盘右边界
		int top = chessH / 2 + (h % 15) / 2; // 棋盘上边界
		int bottom = top + chessH * 14; // 棋盘下边界
		for (int i = 0; i < 15; i++) {
			// 画每条横线
			g.drawLine(left, top + (i * chessH), right, top + (i * chessH));
		}
		for (int i = 0; i < 15; i++) {
			// 画每条竖线
			g.drawLine(left + (i * chessW), top, left + (i * chessW), bottom);
		}
	}

	/**
	 * 面板初始化
	 */
	private void initComponents() {
		gobangModel1 = GobangModel.getInstance(); // 创建棋盘模型的实例对象
		// 添加鼠标监听事件
		addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {// 鼠标点击触发方法
				formMouseClicked(evt);// 运行自定义的事件
			}
		});
		setOpaque(false);// 面板可以是透明的
		setLayout(null);// 使用坐标布局
	}

	/**
	 * 鼠标点击棋盘运行的方法
	 *
	 * @param evt
	 */
	private void formMouseClicked(java.awt.event.MouseEvent evt) {
		// 如果游戏没有开始，或者对方没有开始，或者我没有分配到棋子，或者没有轮到我下棋
		if (!start || !isTowardsStart() || myColor == 0 || !turn) {
			return;
		}
		Point point = evt.getPoint();// 获得鼠标在棋盘上的位置
		int xindex = point.x / chessWidth;// 鼠标位置除棋子宽度 = 棋子位置
		int yindex = point.y / chessHeight;// 鼠标位置除棋子高度 = 棋子位置
		byte[][] chessmanArray = gobangModel1.getChessmanArray();// 获得棋盘数组
		if (chessmanArray[xindex][yindex] == 0) {// 如果鼠标位置上没有棋子
			turn = !turn;// 我丧失下棋权限
			chessmanArray[xindex][yindex] = (byte) myColor; // 将棋子放入棋盘
			gobangModel1.setChessmanArray(chessmanArray);// 将棋盘数据更新到棋子模型当中
			chessPanel.backButton.setEnabled(false);// 悔棋按钮不可用
			repaint();// 重绘组件
			int winColor = arithmetic(myColor, xindex, yindex);// 判断我下棋之后是否获得胜利,返回胜利的棋子颜色
			pustChessQueue(gobangModel1.getChessmanArray());// 将当前棋局保存到队列中
			// 在判断胜负情况后再发送model中的棋盘数组，因为这个数组可能带有标识连线的棋子数据
			if (winColor != 0 && winColor == myColor) {// 如果棋子胜利并且是和我的棋子颜色一致
				chessPanel.send(ChessPanel.WIN); // 发送胜利代码
				win = true;// 将我胜利的标志设置为true
				chessPanel.reInit();// 重新初始化游戏状态
			}
			chessPanel.send(chessmanArray);// 发送当前棋盘数据
		}
	}

	/**
	 * 机器人将棋子放入棋盘
	 *
	 * @param xindex
	 *            -棋子的横坐标
	 *
	 * @param yindex
	 *            -棋子的纵坐标
	 */
	public void chessForMachine(int xindex, int yindex) {
		try {
			Thread.sleep(1000);// 计算机下棋有1秒延迟
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		byte[][] chessmanArray = gobangModel1.getChessmanArray();// 获取棋盘数组
		turn = !turn;// 玩家恢复下棋的权限
		chessmanArray[xindex][yindex] = GobangModel.BLACK_CHESSMAN;// 在此处下一枚黑棋
		gobangModel1.setChessmanArray(chessmanArray);// 更新棋盘数据
		repaint();// 重绘组件
		int winColor = arithmetic(GobangModel.BLACK_CHESSMAN, xindex, yindex);// 判断黑棋是否胜利
		// 在判断胜负情况后再发送model中的棋盘数组，因为这个数组可能带有标识连线的棋子数据
		if (winColor != 0 && winColor == GobangModel.BLACK_CHESSMAN) {
			chessPanel.send(ChessPanel.WIN); // 发送胜利代码
			win = false;// 玩家胜利状态为false
			towardsWin = true;// 对家（AI）胜利状态为true
			chessPanel.reInit();// 重新初始化游戏状态
		}
	}

	/**
	 * 整理棋盘(重绘棋盘)
	 */
	public void zhengliBoard() {
		repaint();// 重绘组件
	}

	/**
	 * 五子棋算法
	 *
	 * @param n
	 *            - 代表棋子颜色的整数
	 * @param Arow
	 *            - 行编号
	 * @param Acolumn
	 *            - 列编号
	 * @return 胜利一方的棋子颜色的整数
	 */
	public int arithmetic(int n, int Arow, int Acolumn) {
		byte n8 = (byte) (n ^ 8);// 导致胜利的棋子的值
		byte[][] note = gobangModel1.getChessmanArrayCopy();// 获取棋盘所有值的数组
		int BCount = 1;// 记录连子个数，如果超过或等于5个，则胜利
		// 水平查找
		boolean Lbol = true;// 默认向左走可以获胜
		boolean Rbol = true;// 默认向右走可以获胜
		for (int i = 1; i <= 5; i++) {// 以参数棋子为原点，向在水平方向走出5步
			if ((Acolumn + i) > 14) {// 如果棋子超出最大列数
				Rbol = false;// 向右走不会获胜
			}
			if ((Acolumn - i) < 0) {// 如果棋子超出最小列数
				Lbol = false;// 向左走不会获胜
			}
			if (Rbol == true) {// 如果向右走可能会获胜
				// 如果向右走，挨着我的棋子都与我颜色相同
				if (note[Arow][Acolumn + i] == n) {
					++BCount;// 连子数递增
					note[Arow][Acolumn + i] = n8;// 将这些棋子标记为可能导致获胜的棋子
				} else {
					Rbol = false;// 向右走不会获胜
				}
			}
			if (Lbol == true) {// 如果向左走可能会获胜
				// 如果向左走，挨着我的棋子都与我颜色相同
				if (note[Arow][Acolumn - i] == n) {
					++BCount;// 连子数递增
					note[Arow][Acolumn - i] = n8;// 将这些棋子标记为可能导致获胜的棋子
				} else {
					Lbol = false;// 向左走不会获胜
				}
			}
			if (BCount >= 5) {// 如果同类型的棋子数大于等于5个
				note[Arow][Acolumn] = n8;// 参数传来的位置上的棋子也改为可能导致获胜的棋子
				gobangModel1.setChessmanArray(note);// 更新棋盘数据
				repaint();// 重绘棋盘
				return n; // 返回胜利一方的棋子的值
			}
		}

		// 垂直查找
		note = gobangModel1.getChessmanArrayCopy();// 获取棋牌的值（未发生更改的值）
		boolean Ubol = true;// 默认向上走可以获胜
		boolean Dbol = true;// 默认向下走可以获胜
		BCount = 1;// 连子个数初始化
		for (int i = 1; i <= 5; i++) {// 以参数棋子为原点，向在垂直方向走出5步
			if ((Arow + i) > 14) {// 如果超出棋盘的最大行数
				Dbol = false;// 向下走不会获胜
			}
			if ((Arow - i) < 0) {// 如果超出棋盘的最小行数
				Ubol = false;// 向上走不会获胜
			}
			if (Dbol == true) {// 如果向下走可以获胜
				// 如果向下走，挨着我的棋子都与我颜色相同
				if (note[Arow + i][Acolumn] == n) {
					++BCount;// 连子数递增
					note[Arow + i][Acolumn] = n8;// 将这些棋子标记为可能导致获胜的棋子
				} else {
					Dbol = false;// 向下走不会获胜
				}
			}
			if (Ubol == true) {// 如果向上走可能获胜
				// 如果向上走，挨着我的棋子都与我颜色相同
				if (note[Arow - i][Acolumn] == n) {
					++BCount;// 连子数递增
					note[Arow - i][Acolumn] = n8;// 将这些棋子标记为可能导致获胜的棋子
				} else {
					Ubol = false;// 向上走不会获胜
				}
			}
			if (BCount >= 5) { // 如果同类型的棋子大于等于5个
				note[Arow][Acolumn] = n8;// 参数传来的位置上的棋子也改为可能导致获胜的棋子
				gobangModel1.setChessmanArray(note);// 更新棋盘数据
				repaint();// 重绘棋盘
				return n; // 返回胜利一方的棋子
			}
		}

		// 正斜查找
		note = gobangModel1.getChessmanArrayCopy();// 获取棋牌的值（未发生更改的值）
		boolean LUbol = true;// 默认向左上走可以获胜
		boolean RDbol = true;// 默认向右下走可以获胜
		BCount = 1;// 连子个数初始化
		for (int i = 1; i <= 5; i++) {// 以参数棋子为原点，向在正斜方向走出5步
			if ((Arow - i) < 0 || (Acolumn - i < 0)) {// 如果超出左上的斜线
				LUbol = false;// 向左上走不会获胜
			}
			if ((Arow + i) > 14 || (Acolumn + i > 14)) {// 如果超出右下的斜线
				RDbol = false;// 向右下走不会获胜
			}
			if (LUbol == true) {// 如果向左上走可以获胜
				// 如果向左上走，挨着我的棋子都与我颜色相同
				if (note[Arow - i][Acolumn - i] == n) {// 如果左上斜线上有相同类型的棋子
					++BCount;// 连子数递增
					note[Arow - i][Acolumn - i] = n8;// 将这些棋子标记为可能导致获胜的棋子
				} else {
					LUbol = false;// 向左上走不会获胜
				}
			}
			if (RDbol == true) {// 如果向右下走可以获胜
				// 如果向右下走，挨着我的棋子都与我颜色相同
				if (note[Arow + i][Acolumn + i] == n) {// 如果右下斜线上有相同类型的棋子
					++BCount;// 连子数递增
					note[Arow + i][Acolumn + i] = n8;// 将这些棋子标记为可能导致获胜的棋子
				} else {
					RDbol = false;// 向右下走不会获胜
				}
			}
			if (BCount >= 5) {// 如果同类型的棋子大于等于5个
				note[Arow][Acolumn] = n8;// 参数传来的位置上的棋子也改为可能导致获胜的棋子
				gobangModel1.setChessmanArray(note);// 更新棋盘数据
				repaint();// 重绘棋盘
				return n; // 返回胜利一方的棋子
			}
		}
		// 反斜查找
		note = gobangModel1.getChessmanArrayCopy();// 获取棋牌的值（未发生更改的值）
		boolean RUbol = true;// 默认向右上走可以获胜
		boolean LDbol = true;// 默认向左下走可以获胜
		BCount = 1;// 连子个数初始化
		for (int i = 1; i <= 5; i++) {// 以参数棋子为原点，向在反斜方向走出5步
			if ((Arow - i) < 0 || (Acolumn + i > 14)) {// 如果超出右上的斜线
				RUbol = false;// 向右上走不会获胜
			}
			if ((Arow + i) > 14 || (Acolumn - i < 0)) {// 如果超出左下的斜线
				LDbol = false;// 向左下走不会获胜
			}
			if (RUbol == true) {// 如果向右上走可以获胜
				// 如果向右上走，挨着我的棋子都与我颜色相同
				if (note[Arow - i][Acolumn + i] == n) {
					++BCount;// 连子数递增
					note[Arow - i][Acolumn + i] = n8;// 将这些棋子标记为可能导致获胜的棋子
				} else {
					RUbol = false;// 默认向右上走可以获胜
				}
			}
			if (LDbol == true) {// 如果向左下走可以获胜
				// 如果向左下走，挨着我的棋子都与我颜色相同
				if (note[Arow + i][Acolumn - i] == n) {
					++BCount;// 连子数递增
					note[Arow + i][Acolumn - i] = n8;// 将这些棋子标记为可能导致获胜的棋子
				} else {
					LDbol = false;// 默认向左下走可以获胜
				}
			}
			if (BCount >= 5) {// 如果同类型的棋子大于等于5个
				note[Arow][Acolumn] = n8;// 参数传来的位置上的棋子也改为可能导致获胜的棋子
				gobangModel1.setChessmanArray(note);// 更新棋盘数据
				repaint();// 重绘棋盘
				return n;// 返回胜利一方的棋子
			}
		}
		return 0;
	}

	/**
	 * 记录游戏结束前的棋盘记录
	 */
	public void oldRec() {
		oldRec = (Object[]) chessQueue.toArray();// 将游戏队列记录转换成数组
	}

	/**
	 * 获取对手是否已开始
	 *
	 * @return
	 */
	public boolean isTowardsStart() {
		ChessPanel panel = (ChessPanel) getParent();
		return panel.isTowardsStart();
	}

	/**
	 * 获取棋盘记录
	 *
	 * @return 棋盘记录
	 */
	public Object[] getOldRec() {
		return oldRec;
	}

	/**
	 * 获取是否轮到自己下棋
	 *
	 * @return
	 */
	public boolean isTurn() {
		return turn;
	}

	/**
	 * 设置是否轮到自己下棋
	 */
	protected void setTurn(boolean turn) {
		this.turn = turn;
		chessPanel.setTurn(turn);// 下棋面板同步更改下棋权限
	}

	/**
	 * 是否已开始游戏
	 *
	 * @return
	 */
	protected boolean isStart() {
		return start;
	}

	/**
	 * 获取我的棋子颜色
	 *
	 * @return
	 */
	public byte getMyColor() {
		return myColor;
	}

	/**
	 * 设置我的棋子颜色
	 *
	 * @param myColor
	 */
	public void setMyColor(byte myColor) {
		this.myColor = myColor;
	}

	/**
	 * 获取对手是否胜利
	 *
	 * @return
	 */
	public boolean isTowardsWin() {
		return towardsWin;
	}

	/**
	 * 设置对手是否胜利
	 *
	 * @param towardsWin
	 */
	public void setTowardsWin(boolean towardsWin) {
		this.towardsWin = towardsWin;
	}

	/**
	 * 获取我自己是否胜利
	 *
	 * @return
	 */
	public boolean isWin() {
		return win;
	}

	/**
	 * 设置我自己是否胜利
	 *
	 * @param win
	 */
	public void setWin(boolean win) {
		this.win = win;
	}

	/**
	 * 获取我自己是否和棋
	 *
	 * @return
	 */
	public boolean isDraw() {
		return draw;
	}

	/**
	 * 设置是否和棋
	 *
	 * @param draw
	 */
	public void setDraw(boolean draw) {
		this.draw = draw;
	}

	/**
	 * 获取游戏队列记录
	 *
	 * @return
	 */
	public Deque<byte[][]> getChessQueue() {
		return chessQueue;
	}

	/**
	 * 向游戏队列中添加棋局数据 将棋盘数组克隆成一份独立的数组，以免数据冲突
	 *
	 * @param chessmanArray
	 *            -棋盘数据数组
	 */
	public synchronized void pustChessQueue(byte[][] chessmanArray) {
		byte newArray[][] = new byte[15][15];// 创建新的数组
		for (int i = 0; i < newArray.length; i++) {// 循环参数中的棋盘数组
			// 将棋盘数组中的值复制给新数组
			newArray[i] = Arrays.copyOf(chessmanArray[i], newArray[i].length);
		}
		chessQueue.push(newArray);// 将当前棋局保存到队列中
	}
}
