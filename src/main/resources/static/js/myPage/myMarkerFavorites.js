$(document).ready(function() {
    // 검색어 입력 시마다 검색 실행
    $('#searchWord').on('keyup', function() {
        submitSearchForm();
    });

    // 병원 필터 클릭 시
    $('#filterHospital').on('click', function() {
        $('#filter').val('hospital');
        submitSearchForm();
    });

    // 대피소 필터 클릭 시
    $('#filterShelter').on('click', function() {
        $('#filter').val('shelter');
        submitSearchForm();
    });

    // 모두 보기 클릭 시
    $('#filterAll').on('click', function() {
        $('#filter').val('');
        submitSearchForm();
    });

    // 검색 및 필터 폼 제출 함수
    function submitSearchForm() {
        let formData = $('#searchForm').serialize();

        $.ajax({
            url: '/my/myMarkerFavorites/search',
            type: 'GET',
            data: formData,
            dataType: 'html',
            success: function(response) {
                $('#markerTableBody').html($(response).find('#markerTableBody').html());
            },
            error: function() {
                alert("검색 실패");
            }
        });
    }
});
