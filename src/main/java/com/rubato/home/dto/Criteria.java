package com.rubato.home.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Criteria {

	private int pageNum=1; // 현재 페이지 번호-list는 무조건 1페이지부터(최신글)
	private int amount=5; // 한페이지에 출력표시될 글의 수(초기값주기)
	private int startNum; //현재 선택된 페이지에서 시작할 글 번호
	
	public String getQueryString() { //페이지 파라미터 값을 넘김
		
		return String.format("page=%d&pageSize-%d", pageNum, amount);
	}
}
