package memberMVC.member;

import java.io.IOException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@WebServlet("/member/*")
public class MemberController extends HttpServlet {
	MemberDAO memberDAO;
	public void init(ServletConfig config) throws ServletException {
		memberDAO = new MemberDAO();
	}


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doHandle(request, response);
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doHandle(request, response);
	}
	private void doHandle(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=utf-8");
		String nextPage = null;
		String action = request.getPathInfo(); //URL  요청명을 가져옵니다.
		System.out.println("요청 매핑이름 : " + action);
		if(action == null || action.equals("/listMembers.do")) {
			List<MemberVO> memberList = memberDAO.listMembers();
			request.setAttribute("memberList", memberList); //회원정보 바인딩
			nextPage ="/memberInfo/listMembers.jsp";
		}else if(action.equals("/memberForm.do")) {
			nextPage ="/memberInfo/memberForm.jsp";
		}else if(action.equals("/addMember.do")) { //컨트롤러에서 nextPage 포워딩
			String id = request.getParameter("id");
			String pwd = request.getParameter("pwd");
			String name = request.getParameter("name");
			String email = request.getParameter("email");
			MemberVO memberVO = new MemberVO(id, pwd, name, email);
			memberDAO.addMember(memberVO);
			request.setAttribute("msg", "addMember");
	           nextPage = "/member/listMembers.do";
		}else if(action.equals("/modMemberForm.do")) {
			String id = request.getParameter("id");
			MemberVO memFindInfo = memberDAO.findMember(id);
			request.setAttribute("memFindInfo", memFindInfo);
			nextPage="/memberInfo/modMemberForm.jsp";
		}else if(action.equals("/modMember.do")) {
			String id = request.getParameter("id");
			String pwd = request.getParameter("pwd");
			String name = request.getParameter("name");
			String email = request.getParameter("email");
			MemberVO memberVO = new MemberVO(id, pwd, name, email);
			memberDAO.modMember(memberVO);
			request.setAttribute("msg", "modified");
			nextPage = "/member/listMembers.do";
		}else if(action.equals("/delMember.do")) {
			String id = request.getParameter("id");
			memberDAO.delMember(id);
			request.setAttribute("msg", "deleted");
			nextPage ="/member/listMembers.do";
		}else {
			List<MemberVO> memberList = memberDAO.listMembers();
			request.setAttribute("memberList", memberList); //회원정보 바인딩
			nextPage ="/memberInfo/listMembers.jsp";
		}
		
		RequestDispatcher dispatcher = request.getRequestDispatcher(nextPage);
		dispatcher.forward(request, response); //컨트롤러에서 화면 출력하는 listMembers.jsp 포워딩
	}

}
