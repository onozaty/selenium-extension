var AutoExec = {
  init: function() {
    $('#loading').ajaxStart(function() {
      $(this).show();
    });
    $('#loading').ajaxStop(function() {
      $(this).hide();
    });

    $('#runButton').click(AutoExec.runTest);
    AutoExec.render();

    setTimeout(
      function() {
        $('#fadeMessage').fadeOut(4000);
      }, 2000);

    AutoExec.intervalTimerId = setInterval(
      function() {
        AutoExec.render();
      }, 30000);
  },

  render: function() {
    $.ajax({
      url: '/selenium-server/autoexec/command/status?type=json',
      type: 'post',
      dataType: 'json',
      error: function() {
        clearInterval(AutoExec.intervalTimerId);
        alert('server access error.');
      },
      success: function(info) {
        $('#lastUpdate').text(info.nowTime || '-');

        $('#status').text(info.status || '-');
        if (info.status == 'idle') {
          $('#runButton').removeAttr('disabled');
        } else {
          $('#runButton').attr('disabled', 'true');
        }

        $('#result').text(info.result || '-');

        $('#passedCount').text(info.passedCount != null ? info.passedCount : '-');
        $('#failedCount').text(info.failedCount != null ? info.failedCount : '-');
        $('#totalCount').text(info.totalCount != null ? info.totalCount : '-');

        $('#startTime').text(info.startTime || '-');
        $('#endTime').text(info.endTime || '-');

        $('#suitesTable > tbody > tr').not(':first').remove();

        var suites = info.suites || [];
        for (var i = 0, len = suites.length; i < len; i++) {
          var isFinish = suites[i].status == 'passed' || suites[i].status == 'failed';

          var html = ['<tr><td>'];
          if (isFinish) {
            html.push('<a href="', suites[i].resultPath, '" target="_blank">');
          }
          html.push(suites[i].suiteName);
          if (isFinish) {
            html.push('</a>');
          }
          html.push('</td>');
          html.push('<td>', suites[i].browser, '</td>');
          html.push('<td>', suites[i].status, '</td>');
          html.push('</tr>');

          $('#suitesTable > tbody').append(html.join(''));
        }
      }
    });
  },

  runTest: function() {
    $.ajax({
      url: '/selenium-server/autoexec/command/run/async',
      type: 'get',
      dataType: 'text',
      error: function() {
        alert('run test error1.');
      },
      success: function(result) {
        if (result != 'success') {
          alert('run test error2.');
        }
        AutoExec.render();
      }
    });

  }
}
$(AutoExec.init);
