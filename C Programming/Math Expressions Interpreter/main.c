/* main.c */

#include <string.h>

#include "ast.h"
#include "ring.h"
#include "tokenizer.h"

void ast_print(struct AST ast) { print_ast(stdout, &ast); }
void token_print(struct token token) { printf("%s(%" PRId64 ")", TOKENS_STR[token.type], token.value); }

DECLARE_RING(ast, struct AST)
DEFINE_RING(ast, struct AST)
DEFINE_RING_PRINT(ast, ast_print)
DEFINE_RING(token, struct token)
DEFINE_RING_PRINT(token, token_print)

#define RETURN_ERROR(code, msg) return printf(msg), code



static struct AST new_op_by_token(struct token token){
switch (token.type) {
case TOK_NEG: return _unop(UN_NEG,NULL);
case TOK_DIV: return _binop(BIN_DIV, NULL, NULL);
case TOK_MINUS: return _binop(BIN_MINUS, NULL, NULL);
case TOK_PLUS: return _binop(BIN_PLUS, NULL, NULL);
case TOK_MUL: return _binop(BIN_MUL, NULL, NULL);
default: return _lit(token.value);
}

}

static void exec_op(struct ring_token **ops_stack, struct ring_ast **oper_stack){
    if(*ops_stack) {
        struct AST curr_op = new_op_by_token(ring_token_pop_top(ops_stack));
        switch (curr_op.type) {
            case AST_UNOP: {
                struct AST curr_oper = ring_ast_pop_top(oper_stack);
                curr_op.as_unop.operand = newnode(curr_oper);
                ring_ast_push_top(oper_stack, curr_op);
                break;
            }
            case AST_BINOP: {
                struct AST oper_b = ring_ast_pop_top(oper_stack);
                struct AST oper_a = ring_ast_pop_top(oper_stack);
                curr_op.as_binop.left = newnode(oper_a);
                curr_op.as_binop.right = newnode(oper_b);
                ring_ast_push_top(oper_stack, curr_op);
                break;
            }
            default:
                break;;
        }
    }
}


struct AST *build_ast(char *str)
{
  struct ring_token *tokens = NULL;

  if ((tokens = tokenize(str)) == NULL)
    RETURN_ERROR(NULL, "Tokenization error.\n");
  ring_token_print(tokens);

  struct ring_ast *oper_stack = NULL;
  struct ring_token *ops_stack = NULL;

  while(tokens){
      switch (tokens->value.type) {
          case TOK_ERROR: {
              return NULL;
          }
          case TOK_END: {
              while (ops_stack) {
                  exec_op(&ops_stack, &oper_stack);
              }
              break;
          }
          case TOK_OPEN: {
              ring_token_push_top(&ops_stack, tokens->value);
              break;

          }
          case TOK_CLOSE: {
              while(ops_stack->value.type != TOK_OPEN){
                  exec_op(&ops_stack, &oper_stack);
              }
              ring_token_pop_top(&ops_stack);
              break;
          }
          case TOK_LIT: {
              ring_ast_push_top(&oper_stack, new_op_by_token(tokens->value));
              break;
          }
          default: {
              if(ops_stack==NULL || ops_stack->value.type==TOK_OPEN){
                  ring_token_push_top(&ops_stack, tokens->value);
                  break;
              }
              struct AST new_operation = new_op_by_token(tokens->value);
              struct AST last_operation = new_op_by_token(ops_stack->value);
              if(ast_get_priority(&new_operation) < ast_get_priority(&last_operation)){
                  ring_token_push_top(&ops_stack, tokens->value);
                  break;
              }
              while(ops_stack !=NULL && ast_get_priority(&new_operation) >= ast_get_priority(&last_operation)){
                  exec_op(&ops_stack, &oper_stack);
                  if(ops_stack != NULL) {
                      last_operation = new_op_by_token(ops_stack->value);
                  }
              }
              ring_token_push_top(&ops_stack, tokens->value);
              break;
          }
      }
      ring_token_pop_top(&tokens);
  }
  return newnode(oper_stack->value);
}





int main()
{
  /* char *str = "1 + 2 * (2 - -3) + 8"; */
  const int MAX_LEN = 1024;
  char str[MAX_LEN];
  if (fgets(str, MAX_LEN, stdin) == NULL)
    RETURN_ERROR(0, "Input is empty.");

  if (str[strlen(str) - 1] == '\n')
    str[strlen(str) - 1] = '\0';

  struct AST *ast = build_ast(str);

  if (ast == NULL)
    printf("AST build error.\n");
  else
  {
    print_ast(stdout, ast);
    printf("\n\n%s = %" PRId64 "\n", str, calc_ast(ast));
    p_print_ast(stdout, ast);
    printf(" = %" PRId64 "\n", calc_ast(ast));    
  }

  return 0;
}
