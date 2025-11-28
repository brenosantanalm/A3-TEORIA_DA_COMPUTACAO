public enum TokenType {
    // Palavras-chave
    INT, REAL, IF, ELSE, WHILE, PRINT, INPUT,

    // Identificadores e literais
    IDENTIFIER, NUMBER,

    // Operadores
    PLUS, MINUS, STAR, SLASH,
    ASSIGN,          // =
    EQUAL_EQUAL,     // ==
    BANG_EQUAL,      // !=
    GREATER,         // >
    GREATER_EQUAL,   // >=
    LESS,            // <
    LESS_EQUAL,      // <=

    // Símbolos
    SEMICOLON,       // ;
    LPAREN,          // (
    RPAREN,          // )
    LBRACE,          // {
    RBRACE,          // }

    EOF
}
