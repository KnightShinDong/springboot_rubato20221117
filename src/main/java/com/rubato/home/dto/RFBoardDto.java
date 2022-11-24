package com.rubato.home.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RFBoardDto {

	private int rfbnum;
	private String rfbname;
	private String rfbtitle;
	private String rfbcontent;
	private int rfbhit;//조회수
	private String rfbid;//글쓴이의 아이디
	private int rfbreplycount;//첨부된 댓글개수
	private String rfbdate;
	private int fileCount;//첨부된 파일 개수
	
	
	
}
