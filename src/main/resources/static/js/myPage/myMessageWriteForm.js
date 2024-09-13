$(document).ready(function(){
    
    // 친구 검색 버튼을 눌렀을 때 새 창을 열어 친구 목록 검색 창 띄움
    $('#searchId').click(function(){
        window.open(	// 현재 창(부모창) 기준 자식창 새로 열기 - method="get"만 지원, "post"로 하고 싶으면, js 사용해야!
            // open() 함수: 괄호 안 4Param - 경로, 새로 열릴 창 이름(자유), value(default: _blank 새 창), 모양
            // http://localhost:8888/my/message/searchFriend -> 그치만 이렇게 열면, 부모창-자식창 관계가 성립되지 않음!
            'searchFriend',
            'searchFriend',
            'top=150, left=550, width=500, height=450, location=no, titlebar=no');	// 시작 위치 & 크기
    })
})
