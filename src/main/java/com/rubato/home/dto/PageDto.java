package com.rubato.home.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class PageDto {

	private int startPage; //현재화면에서 보여실 시작 페이지 번호
	private int endPage; //현재화면에서 보여실 마지막 페이지 번호
	private boolean prev; //이전페이지
	private boolean next; //다음페이지
	private int total; //전체 페이지 개수
	
	private Criteria cri;
	
	public PageDto(Criteria cri, int total) {
		
		this.cri = cri;
		this.total = total;
		
		this.endPage = (int)Math.ceil((cri.getPageNum()/5.0)) * 5; //MAth.ceil -->올림
																	//10페이직 씩은 10으로 곱하고 10으로 나눔
		this.startPage = this.endPage -4;
		
		int realEnd= (int)Math.ceil(((double)total)/cri.getAmount()); 
										//실수 나누기 정수로 해야된다.
		if(realEnd < this.endPage) {
			this.endPage = realEnd;
		}
		
		this.prev = this.startPage > 1;
		this.next = realEnd > this.endPage; 
		//이전, 다음 페이지 버튼 출력 여부 결정
		
		
		
	}
	
}
