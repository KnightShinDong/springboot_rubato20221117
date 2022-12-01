package com.rubato.home.dao;

import java.util.ArrayList;

import com.rubato.home.dto.Criteria;
import com.rubato.home.dto.FileDto;
import com.rubato.home.dto.RFBoardDto;
import com.rubato.home.dto.RMemberDto;
import com.rubato.home.dto.RReplyDto;

public interface IDao {
	//멤버 관련
	public void memberJoinDao(String mid, String mpw, String mname, String memail);
	public int checkUserIdDao(String mid);
	public int checkUserIdAndPwDao(String mid, String mpw);
	
	//게시판 관련
	public void rfbWriteDao(String rfbname, String rfbtitle, String rfbcontent,String rfbid,int fileCount);
	public RMemberDto rfbNameDao(String mid);
	public ArrayList<RFBoardDto> rfbListDao(Criteria cri);
	public RFBoardDto rfbViewDao(String rfbnum);
	public int rfboardAllCountDao();
	public void deleteDao(String rfbnum);
	public void hitDao(String rfbnum);
	public void modifyDao( String rfbtitle, String fbcontent,String rfbnum);
	
	//댓글 관련
	public void rrWriteDao(String rrorinum, String rrid, String rrcontent);//새댓글입력
	public ArrayList<RReplyDto> rrListDao(String rrorinum);
	//댓글 등록시 해당글의 댓글갯수 1증가
	public void rrCountDao(String rrcountnum);
	public void rrDeleteDao(String rrnum);
	public void rrBBDao(String rrcountnum);
	
	//게시판 검색 관련
	public ArrayList<RFBoardDto> rfbSearchTitleListDao(String searchKey);
	public ArrayList<RFBoardDto> rfbSearchContentListDao(String searchKey);
	public ArrayList<RFBoardDto> rfbSearchWriterListDao(String searchKey);
	
	//파일업로드 관련
	public void fileInfoInsertDao(int boardNum, String fileOriName, String fileName, String fileExtension, String fileUrl );
	public ArrayList<RFBoardDto> boardLatestInfoDao(String rfbId);
	//현재 파일이 첨부된 글을 쓴아이디로 검색된 글 목록
	public FileDto getFileInfoDao(String rfbnum);
	
	public int boardAllCount();
	
}
