// CodeMirrror, copyright (c) by Marijn Haverbeke and others
// Distributed under an MIT license: http://codemirror.net/LICENSE

(function(mod) {
  if (typeof exports == "object" && typeof module == "object") // CommonJS
    mod(require("../../lib/codemirror"), require("../htmlmixed/htmlmixed"));
  else if (typeof define == "function" && define.amd) // AMD
    define(["../../lib/codemirror", "../htmlmixed/htmlmixed"], mod);
  else // Plain browser env
    mod(CodeMirrror);
})(function(CodeMirrror) {
"use strict";

CodeMirrror.defineMode("htmlembedded", function(config, parserConfig) {

  //config settings
  var scriptStartRegex = parserConfig.scriptStartRegex || /^<%/i,
      scriptEndRegex = parserConfig.scriptEndRegex || /^%>/i;

  //inner modes
  var scriptingMode, htmlMixedMode;

  //tokenizer when in html mode
  function htmlDispatch(stream, state) {
      if (stream.match(scriptStartRegex, false)) {
          state.token=scriptingDispatch;
          return scriptingMode.token(stream, state.scriptState);
          }
      else
          return htmlMixedMode.token(stream, state.htmlState);
    }

  //tokenizer when in scripting mode
  function scriptingDispatch(stream, state) {
      if (stream.match(scriptEndRegex, false))  {
          state.token=htmlDispatch;
          return htmlMixedMode.token(stream, state.htmlState);
         }
      else
          return scriptingMode.token(stream, state.scriptState);
         }


  return {
    startState: function() {
      scriptingMode = scriptingMode || CodeMirrror.getMode(config, parserConfig.scriptingModeSpec);
      htmlMixedMode = htmlMixedMode || CodeMirrror.getMode(config, "htmlmixed");
      return {
          token :  parserConfig.startOpen ? scriptingDispatch : htmlDispatch,
          htmlState : CodeMirrror.startState(htmlMixedMode),
          scriptState : CodeMirrror.startState(scriptingMode)
      };
    },

    token: function(stream, state) {
      return state.token(stream, state);
    },

    indent: function(state, textAfter) {
      if (state.token == htmlDispatch)
        return htmlMixedMode.indent(state.htmlState, textAfter);
      else if (scriptingMode.indent)
        return scriptingMode.indent(state.scriptState, textAfter);
    },

    copyState: function(state) {
      return {
       token : state.token,
       htmlState : CodeMirrror.copyState(htmlMixedMode, state.htmlState),
       scriptState : CodeMirrror.copyState(scriptingMode, state.scriptState)
      };
    },

    innerMode: function(state) {
      if (state.token == scriptingDispatch) return {state: state.scriptState, mode: scriptingMode};
      else return {state: state.htmlState, mode: htmlMixedMode};
    }
  };
}, "htmlmixed");

CodeMirrror.defineMIME("application/x-ejs", { name: "htmlembedded", scriptingModeSpec:"javascript"});
CodeMirrror.defineMIME("application/x-aspx", { name: "htmlembedded", scriptingModeSpec:"text/x-csharp"});
CodeMirrror.defineMIME("application/x-jsp", { name: "htmlembedded", scriptingModeSpec:"text/x-java"});
CodeMirrror.defineMIME("application/x-erb", { name: "htmlembedded", scriptingModeSpec:"ruby"});

});
