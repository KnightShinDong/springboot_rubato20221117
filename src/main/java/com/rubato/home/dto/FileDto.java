package com.rubato.home.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data

public class FileDto {

	private int fileNum;//파일고유번호-시퀀스
	private int boardNum;//파일첨부된 원글 게시판 글번호
	private String fileOriName;//첨부된 파일의 원래이름
	private String fileName;//첨분된 파일의 변경된 랜덤이름
	private String fileExtension;// 첨부된 파일의 확장자
	private String fileUrl; //첨부된 파일이 실제로 저장된 서버의 저장경로
	
}
