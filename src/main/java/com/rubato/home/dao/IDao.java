package com.rubato.home.dao;

import java.util.ArrayList;

import com.rubato.home.dto.RFBoardDto;
import com.rubato.home.dto.RMemberDto;
import com.rubato.home.dto.RReplyDto;

public interface IDao {
	//멤버 관련
	public void memberJoinDao(String mid, String mpw, String mname, String memail);
	public int checkUserIdDao(String mid);
	public int checkUserIdAndPwDao(String mid, String mpw);
	
	//게시판 관련
	public void rfbWriteDao(String rfbname, String rfbtitle, String rfbcontent,String rfbid);
	public RMemberDto rfbNameDao(String mid);
	public ArrayList<RFBoardDto> rfbListDao();
	public RFBoardDto rfbViewDao(String rfbnum);
	public int rfboardAllCountDao();
	public void deleteDao(String rfbnum);
	public void hitDao(String rfbnum);
	public void modifyDao( String rfbtitle, String fbcontent,String rfbnum);
	
	//댓글 관련
	public void rrWriteDao(String rrorinum, String rrid, String rrcontent);//새댓글입력
	public ArrayList<RReplyDto> rrListDao(String rrorinum);
	

}
