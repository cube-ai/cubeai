// CodeMirrror, copyright (c) by Marijn Haverbeke and others
// Distributed under an MIT license: http://codemirror.net/LICENSE

(function() {
  CodeMirrror.defineMode("markdown_with_stex", function(){
    var inner = CodeMirrror.getMode({}, "stex");
    var outer = CodeMirrror.getMode({}, "markdown");

    var innerOptions = {
      open: '$',
      close: '$',
      mode: inner,
      delimStyle: 'delim',
      innerStyle: 'inner'
    };

    return CodeMirrror.multiplexingMode(outer, innerOptions);
  });

  var mode = CodeMirrror.getMode({}, "markdown_with_stex");

  function MT(name) {
    test.mode(
      name,
      mode,
      Array.prototype.slice.call(arguments, 1),
      'multiplexing');
  }

  MT(
    "stexInsideMarkdown",
    "[strong **Equation:**] [delim $][inner&tag \\pi][delim $]");
})();
