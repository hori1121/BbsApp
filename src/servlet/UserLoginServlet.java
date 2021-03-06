package servlet;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import crypt.Base64EnCrypt;
import model.ThreadBean;
import model.ThreadLogic;
import model.UserBean;
import model.UserLogic;


@WebServlet("/UserLoginServlet")
public class UserLoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		String userName = request.getParameter("name");

		//passwordの暗号化
		Base64EnCrypt base64EnCrypt = new Base64EnCrypt();
		String userPassword = base64EnCrypt.enCodePassword(request.getParameter("password"));

		//ログイン処理を行う
		UserBean user = new UserBean(userName,userPassword);
		UserLogic bo = new UserLogic();
		boolean isUser = false;
		try {
			isUser = bo.executeUserLogin(user);
		} catch (URISyntaxException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

		HttpSession session = request.getSession();	//セッションスコープに保存

		if(isUser) {
			//スレッドを全件取得しリクエストスコープに保存
			ThreadLogic tl = new ThreadLogic();
			List<ThreadBean>threadList = tl.executeFindAllThread();
			request.setAttribute("threadList", threadList);

			request.setAttribute("genreimg", String.valueOf(0));//プログラミング言語毎の画像表示のため

			session.setAttribute("name", userName);
			request.getRequestDispatcher("WEB-INF/jsp/userMainMenu.jsp").forward(request, response);//ユーザーメニュー画面へ
		}else {
			request.setAttribute("message", "IDかパスワードが間違っています");
			request.getRequestDispatcher("userLogin.jsp").forward(request, response);
		}

	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.sendRedirect("userLogin.jsp");
	}

}
