
let myLat;
let myLong;

$(document).ready(function() {

    // 도쿄역 JR선 위도(myLat), 경도(myLong)
    myLat = 35.68198566590147;
    myLong = 139.76721063068848;

    /*
    navigator.geolocation.getCurrentPosition((position) => {
        alert("getCurrentPosition_Success");
        myLat = position.coords.latitude;
        myLong = position.coords.longitude;
    })*/

    // 필터 버튼 클릭 시 필터 조건을 설정하고 폼을 제출 ====================================
    $('#filterAll').click(function() {
        $('#filter').val(''); // 필터를 제거
        //$('#page').val(1); // 페이지를 1로 초기화
        fetchMarkers();
    });

    $('#filterHospital').click(function() {
        $('#filter').val('hospital'); // 병원 필터
        //$('#page').val(1); // 페이지를 1로 초기화
        fetchMarkers();
    });

    $('#filterShelter').click(function() {
        $('#filter').val('shelter'); // 대피소 필터
        //$('#page').val(1); // 페이지를 1로 초기화
        fetchMarkers();
    });

    // 검색어 입력 시 실시간 검색 (keyup 이벤트) =========================================
    $('#searchWord').on('keyup', function() {
        //$('#page').val(1); // 페이지를 1로 초기화
        fetchMarkers();
    });

    // 정렬 버튼 클릭 시 정렬 조건을 설정하고 폼을 제출 ====================================
    $('#sortByDistance').click(function() {
        $('#sortBy').val('sortByDistance'); // 거리 정렬 설정
        //$('#page').val(1); // 페이지를 1로 초기화
        fetchMarkers();
    });

    $('#sortByName').click(function() {
        $('#sortBy').val('sortByName'); // 거리 정렬 설정
        //$('#page').val(1); // 페이지를 1로 초기화
        fetchMarkers();
    });

    // 닉네임 수정 / 등록 기능 ===========================================================
    $('#markerTableBody').on('click', 'a.nickname-exists', function (event) {
        event.preventDefault();
        let favoriteId = $(this).closest('tr').find('input[name="favoriteId"]').val();
        let currentNickname = $(this).text();

        if(confirm(`닉네임을 수정하시겠습니까?\n현재 닉네임: ${currentNickname}`)) {
            let newNickname = prompt('새로운 닉네임을 입력하세요:', currentNickname);
            if(newNickname !== null) {
                updateNickname(favoriteId, newNickname);
            }
        }
    });

    // 닉네임이 없는 경우 클릭 시 등록할 수 있는 알림창을 띄움
    $('#markerTableBody').on('click', 'a.nickname-missing', function(event) {
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
        $.ajax({
            url: '/my/myMarkerFavorites/updateNickname',
            type: 'POST',
            data: {
                favoriteId: favoriteId,
                nickname: newNickname,
            },
            success: function(response) {
                fetchMarkers();
                alert('닉네임이 성공적으로 업데이트되었습니다. ');
            },
            error: function (error) {
                alert('닉네임 업데이트 중 오류가 발생했습니다. ');
                console.log('Error:', error);
            }
        });
    }

    // =================================================================================

    // Ajax로 리스트 데이터를 불러오는 함수
    function fetchMarkers() {
        let formData = $('#pagingForm').serialize();
        formData += `&latitude=${myLat}&longitude=${myLong}`;
        $.ajax({
            url: '/my/myMarkerFavorites', // 데이터를 요청할 URL
            type: 'GET',
            data: formData, // 폼 데이터를 직렬화하여 전송
            success: function(response) {
                // 서버로부터 받은 HTML 중 리스트 부분만 교체
                $('#markerTableBody').html($(response).find('#markerTableBody').html());
                $('#navigator').html($(response).find('#navigator').html());
            },
            error: function(error) {
                console.log('Error:', error);
            }
        });
    }
});
