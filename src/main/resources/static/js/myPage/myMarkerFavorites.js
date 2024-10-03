let myLat;
let myLong;

$(document).ready(function () {

    // 도쿄역 JR선 위도(myLat), 경도(myLong)
    myLat = 35.68198566590147;
    myLong = 139.76721063068848;
    //console.log('초기 위치 설정:', '위도:', myLat, '경도:', myLong);

    // 필터 버튼 클릭 시 필터 조건을 설정하고 마커 데이터를 가져옵니다.
    $('#filterAll').click(function (event) {
        event.preventDefault();
        //console.log('모두 보기 필터 클릭됨');
        $('#filter').val(''); // 필터를 제거
        fetchMarkers();
    });

    $('#filterHospital').click(function (event) {
        event.preventDefault();
        //console.log('병원 필터 클릭됨');
        $('#filter').val('hospital'); // 병원 필터
        fetchMarkers();
    });

    $('#filterShelter').click(function (event) {
        event.preventDefault();
        //console.log('대피소 필터 클릭됨');
        $('#filter').val('shelter'); // 대피소 필터
        fetchMarkers();
    });

    // 검색어 입력 시 실시간 검색
    $('#searchWord').on('keyup', function () {
        //console.log('검색어 입력됨:', $(this).val());
        fetchMarkers();
    });

    // 정렬 버튼 클릭 시 정렬 조건을 설정하고 마커 데이터를 가져옵니다.
    $('#sortByDistance').click(function (event) {
        event.preventDefault();
        //console.log('거리순 정렬 클릭됨');
        $('#sortBy').val('sortByDistance'); // 거리 정렬 설정
        fetchMarkers();
    });

    $('#sortByName').click(function (event) {
        event.preventDefault();
        //console.log('이름순 정렬 클릭됨');
        $('#sortBy').val('sortByName'); // 이름 정렬 설정
        fetchMarkers();
    });

    // 닉네임 수정 / 등록 기능
    $('#markerTableBody').on('click', 'a.nickname-exists, a.nickname-missing', function (event) {
        event.preventDefault();
        let favoriteId = $(this).closest('tr').find('input[name="favoriteId"]').val();
        let currentNickname = $(this).text();
        //console.log('닉네임 클릭됨:', 'favoriteId:', favoriteId, '현재 닉네임:', currentNickname);

        if (confirm(`닉네임을 수정하시겠습니까?\n현재 닉네임: ${currentNickname}`)) {
            let newNickname = prompt('새로운 닉네임을 입력하세요:', currentNickname);
            if (newNickname !== null) {
                updateNickname(favoriteId, newNickname);
            }
        }
    });

    // 닉네임이 없는 경우 클릭 시 등록할 수 있는 알림창을 띄움
    $('#markerTableBody').on('click', 'a.nickname-missing', function (event) {
        event.preventDefault(); // 기본 링크 클릭 동작 방지
        let favoriteId = $(this).closest('tr').find('input[name="favoriteId"]').val();

        if (confirm('새로운 닉네임을 지으시겠습니까?')) {
            let newNickname = prompt('새로운 닉네임을 입력하세요:');
            if (newNickname) {
                updateNickname(favoriteId, newNickname);
            }
        }
    });

    // 닉네임 수정/등록을 서버로 전달하는 함수
    function updateNickname(favoriteId, newNickname) {
        //console.log('닉네임 업데이트 요청:', 'favoriteId:', favoriteId, '새 닉네임:', newNickname);
        $.ajax({
            url: '/my/myMarkerFavorites/updateNickname',
            type: 'POST',
            data: {
                favoriteId: favoriteId,
                nickname: newNickname,
            },
            success: function (response) {
                //console.log('닉네임 업데이트 성공:', response);
                fetchMarkers();
                alert('닉네임이 성공적으로 업데이트되었습니다.');
            },
            error: function (error) {
                //console.log('닉네임 업데이트 오류:', error);
                alert('닉네임 업데이트 중 오류가 발생했습니다.');
            }
        });
    }

    // =================================================================================

    // Ajax로 리스트 데이터를 불러오는 함수
    function fetchMarkers() {
        let formData = $('#pagingForm').serialize();
        formData += `&latitude=${myLat}&longitude=${myLong}`;
        //console.log('마커 데이터 요청:', formData);

        $.ajax({
            url: '/my/myMarkerFavorites', // 데이터를 요청할 URL
            type: 'GET',
            data: formData, // 폼 데이터를 직렬화하여 전송
            success: function (response) {
                //console.log('마커 데이터 수신 성공');
                // 서버로부터 받은 HTML 중 리스트 부분만 교체
                $('#markerTableBody').html($(response).find('#markerTableBody').html());
                $('#navigator').html($(response).find('#navigator').html());
            },
            error: function (error) {
                //console.log('마커 데이터 수신 오류:', error);
            }
        });
    }
});
