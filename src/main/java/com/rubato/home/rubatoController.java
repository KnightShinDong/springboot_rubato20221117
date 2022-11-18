package com.rubato.home;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.catalina.connector.Response;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.rubato.home.dao.IDao;
import com.rubato.home.dto.RFBoardDto;
import com.rubato.home.dto.RMemberDto;
import com.rubato.home.dto.RReplyDto;

@Controller
public class rubatoController {

	@Autowired
	private SqlSession sqlSession;
	
	@RequestMapping(value = "index")
	public String index() {
		
		return "index";
	}
	
	
	
	@RequestMapping(value = "board_write")
	public String board_write(HttpSession session, HttpServletResponse response) {
		
		String sessionId = (String)session.getAttribute("sessionId");
		if(sessionId == null) {//참이면 로그인이 안된 상태
			PrintWriter out;
			try {
				response.setContentType("text/html;charset=utf-8");
				out = response.getWriter();
				out.println("<script>alert('로그인하지 않으면 글을 쓰실수 없습니다!');history.go(-1);</script>");
				out.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		return "board_write";
	}
	
	@RequestMapping(value = "member_join")
	public String member_join() {
		
		return "member_join";
	}
	
	@RequestMapping(value = "joinOk")
	public String joinOk(HttpServletRequest request, HttpSession session) {
		
		IDao dao = sqlSession.getMapper(IDao.class);
		String mid = request.getParameter("mid");
		String mpw = request.getParameter("mpw");
		String mname = request.getParameter("mname");
		String memail = request.getParameter("memail");
		
		dao.memberJoinDao(mid, mpw, mname, memail);
		
		session.setAttribute("memberId", mid);
		
		
		return "redirect:index";
	}
	
	@RequestMapping(value = "loginOk")
	public String loginOk(HttpServletRequest request, HttpSession session) {
		
		IDao dao = sqlSession.getMapper(IDao.class);
		String memberId = request.getParameter("mid");
		String memberPw = request.getParameter("mpw");
	
		
		
		//int checkIdFlag = dao.checkUserIdDao(memberid);
		int checkIdFlag = dao.checkUserIdAndPwDao(memberId, memberPw);
		
		if(checkIdFlag == 1) { //1이면 로그인 OK
			session.setAttribute("memberId", memberId);//로그인시킴
			String sid = (String)session.getAttribute("memberId");
			RMemberDto dto = dao.rfbNameDao(sid);
			session.setAttribute("memberName", dto.getMname());
		}
		return "redirect:index";
	}

	@RequestMapping(value = "logOut")
	public String logOut(HttpSession session) {
		
		session.invalidate();
		
		return "redirect:index";
	}
	@RequestMapping(value = "writeOk")
	public String writeOk(HttpSession session, HttpServletRequest request) {
		
		IDao dao = sqlSession.getMapper(IDao.class);
		String rfbname = request.getParameter("rfbname");
		String rfbtitle = request.getParameter("rfbtitle");
		String rfbcontent = request.getParameter("rfbcontent");
		String rfbid = request.getParameter("rfbid");
		
		dao.rfbWriteDao(rfbname, rfbtitle, rfbcontent, rfbid);
		//String sessionId= session.getAttribute("memberId");
		//글쓴이의 아이디는 현재 로그인된 유저의 아이디이므로 세션에서 가져와서 전달
		//dao.rfbWriteDao(rfbname, rfbtitle, rfbcontent, sessionId);
		
		return "redirect:board_list";
	}
	
	@RequestMapping(value = "board_list")
	public String board_list(Model model) {
		
		IDao dao = sqlSession.getMapper(IDao.class);
		
		ArrayList<RFBoardDto> dtos = dao.rfbListDao();
		int boardCount = dao.rfboardAllCountDao();
		model.addAttribute("dtos", dtos);
		model.addAttribute("count", boardCount);
		
		return "board_list";
	}

	@RequestMapping(value = "board_view")
	public String board_view(Model model, HttpServletRequest request) {
		
		IDao dao = sqlSession.getMapper(IDao.class);
		String rfbnum = request.getParameter("rfbnum");
		dao.hitDao(rfbnum);//조회수증가
		
		RFBoardDto dto = dao.rfbViewDao(rfbnum);
		ArrayList<RReplyDto> rdto = dao.rrListDao(rfbnum);
		model.addAttribute("view", dto);
		model.addAttribute("rrlist", rdto);
		return "board_view";
	}
	
	@RequestMapping(value = "deleteView")
	public String deleteView(HttpServletRequest request) {
		IDao dao = sqlSession.getMapper(IDao.class);
		String rfbnum = request.getParameter("rfbnum");
		dao.deleteDao(rfbnum);
		
		
		return "redirect:board_list";
	}
	
	
	@RequestMapping(value = "replyOk")
	
	public String replyOk(HttpServletResponse response,HttpServletRequest request,HttpSession session, Model model) {
	
		
		
		String rrorinum = request.getParameter("rfbnum");//댓글이 달린 원글의 번호
		String rrcontent = request.getParameter("rrcontent");
		
		String sessionId=(String) session.getAttribute("memberId");
		
		if(sessionId == null) {//참이면 로그인이 안된 상태
			PrintWriter out;
			try {
				response.setContentType("text/html;charset=utf-8");
				out = response.getWriter();
				out.println("<script>alert('로그인하지 않으면 글을 쓰실수 없습니다!');history.go(-1);</script>");
				out.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			} else {
				
				IDao dao = sqlSession.getMapper(IDao.class);
				dao.rrWriteDao(rrorinum, sessionId, rrcontent); //댓글쓰기
				RFBoardDto dto = dao.rfbViewDao(rrorinum);
				ArrayList<RReplyDto> rdto = dao.rrListDao(rrorinum);
				
				model.addAttribute("rrlist",rdto);//해당글에달린 댓글리스트
				
				model.addAttribute("view", dto);//원글의 게시글 내용
			}
		
		return "board_view";
	}
	
	
	
	@RequestMapping(value = "modify")
	public String modify(HttpServletRequest request, Model model) {
		IDao dao = sqlSession.getMapper(IDao.class);
		String rfbnum = request.getParameter("rfbnum");
		String rftitle = request.getParameter("rfbtitle");
		String rfbcontent = request.getParameter("rfbcontent");
		
		dao.modifyDao(rftitle, rfbcontent, rfbnum);
				
		return "redirect:board_list";
	}
	@RequestMapping(value = "modifyView")
	public String modifyView(HttpServletRequest request, Model model) {
		IDao dao = sqlSession.getMapper(IDao.class);
		String rfbnum = request.getParameter("rfbnum");
		
		RFBoardDto dto = dao.rfbViewDao(rfbnum);
		
		model.addAttribute("view", dto);
		
		return "modifyView";
	}
	
	
	
}
