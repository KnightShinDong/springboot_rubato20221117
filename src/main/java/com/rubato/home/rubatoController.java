package com.rubato.home;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import com.rubato.home.dao.IDao;
import com.rubato.home.dto.Criteria;
import com.rubato.home.dto.FileDto;
import com.rubato.home.dto.PageDto;
import com.rubato.home.dto.RFBoardDto;
import com.rubato.home.dto.RMemberDto;
import com.rubato.home.dto.RReplyDto;

@Controller
public class rubatoController {

	@Autowired
	private SqlSession sqlSession;
	
	
	@RequestMapping(value = "/")
	public String home() {
	return "redirect:index";
	}
	
	@RequestMapping(value = "index")
	public String index(Model model, Criteria cri) {
		
		IDao dao = sqlSession.getMapper(IDao.class);
		
		ArrayList<RFBoardDto> dtos = dao.rfbListDao(cri);
		int boardCount = dao.rfboardAllCountDao();
		
		//List<RFBoardDto> dtos = dao.rfbListDao();
		//dtos = dtos.subList(0,4);
		//범위 에러가 나면 트라이 캐치문으로 글의 개수가 적을때 나타나는 예외처리 필요
		
		
		
		
		
		model.addAttribute("dtos", dtos);
		model.addAttribute("count", boardCount);
		
		
		
		return "index";
	}
	
	
	
	@RequestMapping(value = "board_write")
	public String board_write(HttpSession session, HttpServletResponse response) {
		
		String sessionId = (String)session.getAttribute("memberId");
		
		
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
	public String writeOk(HttpSession session, HttpServletRequest request, @RequestPart MultipartFile files) throws IllegalStateException, IOException {
																			//첨부된 파일을 파일체로 받아옴
		IDao dao = sqlSession.getMapper(IDao.class);
		String rfbname = request.getParameter("rfbname");
		String rfbtitle = request.getParameter("rfbtitle");
		String rfbcontent = request.getParameter("rfbcontent");
		String rfbid = request.getParameter("rfbid");
		
		
		if(files.isEmpty()) {	//파일 첨부여부 확인
			dao.rfbWriteDao(rfbname, rfbtitle, rfbcontent, rfbid,0);
			//String sessionId= session.getAttribute("memberId");
			//글쓴이의 아이디는 현재 로그인된 유저의 아이디이므로 세션에서 가져와서 전달
			//dao.rfbWriteDao(rfbname, rfbtitle, rfbcontent, sessionId);
		} else {
			dao.rfbWriteDao(rfbname, rfbtitle, rfbcontent, rfbid,1);
			
			ArrayList<RFBoardDto> latestBoard= dao.boardLatestInfoDao(rfbid);
			RFBoardDto dto = latestBoard.get(0);
			int rfbnum = dto.getRfbnum();
			
			
			//파일첨부
			String fileOriName = files.getOriginalFilename(); //첨부된 파일의 원래이름
			String fileExtension = FilenameUtils.getExtension(fileOriName).toLowerCase();//첨부된 파일의 확장자뽑아서 저장
																			//확장자 추출 후 소문자로 강제 변경.t oLowerCase()
			File destinationFile; //java.io 패키지 클래스 임포트
			String destinationFileName; //실제 서버에 저장된 파일의 변경된 이름이 저장될 변수 선언
			String fileUrl = "C:/springboot_workspace/rubatoProject001-20221117/src/main/resources/static/uploadfiles/";
			// 첨부된 파일이 저장될 서버의 실제 폴더 경로 url  주소 /로 바까주고 마지막에 / 꼭 추가!
			
			
			do {
			destinationFileName = RandomStringUtils.randomAlphanumeric(32) + "."+ fileExtension;
			//파일변수명(렌덤 숫자와영문대소문자 32개)뽑기 - 랜덤32자+ . + 확장자
			// 알파벳대소문자와 숫자를 포함한 랜덤 32자 문자열 생성 후 . 을 구분자로 원본 파일의 확장자를 연결->실제 서버에 저장할 파일의 이름
		
			destinationFile = new File(fileUrl+destinationFileName);
			} while(destinationFile.exists());
			//혹시 같은 이름의 파일이름이 존재하는지 확인
			
			destinationFile.getParentFile().mkdir();
			files.transferTo(destinationFile); // 업로드된 파일이 지정한 폴더로 이동완료!!
			//add thorws declaration	
			
			dao.fileInfoInsertDao(rfbnum, fileOriName, destinationFileName, fileExtension, fileUrl);
			
		
		}
		
		
		return "redirect:board_list";
	}
	
	@RequestMapping(value = "board_list")
	public String board_list(HttpServletRequest request, Model model, Criteria cri) {
		
		
		int pageNumInt= 0;
		if(request.getParameter("pageNum") == null) {
			pageNumInt = 1;
			cri.setPageNum(pageNumInt);
			
		} else {
			pageNumInt = Integer.parseInt(request.getParameter("pageNum"));
		//처음 넘어온 값이 null값이라  if문사용
			cri.setPageNum(pageNumInt);
		}
		
		
		
		IDao dao = sqlSession.getMapper(IDao.class);
		int totalRecord = dao.boardAllCount(); 
		ArrayList<RFBoardDto> dtos = dao.rfbListDao(cri);
		int boardCount = dao.rfboardAllCountDao();
		
		cri.setStartNum(cri.getPageNum()-1 * cri.getAmount());
		
		PageDto pageDto = new PageDto(cri, totalRecord);
		
		model.addAttribute("pageMaker", pageDto);//페이징관련
		model.addAttribute("currPage", pageNumInt);
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
		
		
		FileDto fileDto = dao.getFileInfoDao(rfbnum);
		model.addAttribute("fileDto", fileDto);
		
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
				
				dao.rrCountDao(rrorinum);//해당글의 총개수 증가 
				
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
	
	@RequestMapping(value = "rrdelete")
	public String rrdelete(HttpServletRequest request,Model model) {
		IDao dao = sqlSession.getMapper(IDao.class);
		String rrnum = request.getParameter("rrnum");
		String rrorinum = request.getParameter("rrorinum");
		
		dao.rrDeleteDao(rrnum);
		dao.rrBBDao(rrorinum);
		
		RFBoardDto dto = dao.rfbViewDao(rrorinum);
		ArrayList<RReplyDto> rdto = dao.rrListDao(rrorinum);
		model.addAttribute("rrlist",rdto);
		model.addAttribute("view", dto);
		
		
		
		return "board_view";
	}
	
	@RequestMapping(value = "search_list")
	public String search_list(HttpServletRequest request, Model model) {
		IDao dao = sqlSession.getMapper(IDao.class);
		String sOption = request.getParameter("searchOption");
		// title, content, writer 3개중 한개값
		String sKey = request.getParameter("searchKey");
		//제목,내용,글쓴이 에 포함된 검색 키워드 낱말
		
		ArrayList<RFBoardDto> dtos = null;
		
		if(sOption.equals("title") ) {
			dtos = dao.rfbSearchTitleListDao(sKey);
		} else if(sOption.equals("content") ){
			dtos = dao.rfbSearchContentListDao(sKey);
		} else if (sOption.equals("writer") ){
			dtos = dao.rfbSearchWriterListDao(sKey);
		}
		
		model.addAttribute("dtos", dtos);
		
		model.addAttribute("Count", dtos.size());
		//검색 결과 게시물의 개수 반환
	
		
		
		return "board_list";
	}
	
	@RequestMapping(value = "file_down")
	public String file_down(HttpServletRequest request, Model model, HttpServletResponse response) {
		String rfbnum = request.getParameter("rfbnum"); // 파일이 첨부된 원글 번호
		IDao dao = sqlSession.getMapper(IDao.class);
		
		FileDto fileDto = dao.getFileInfoDao(rfbnum);
		
		String fileName = fileDto.getFileName();
		
		
		PrintWriter out;
		
		try {
			response.setContentType("text/html;charset=utf-8");
			out = response.getWriter();
			out.println("<script>window.location.href='/resources/uploadfiles/"+ fileName + "'</script>");
			out.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return "filedown";
	}
	
}
