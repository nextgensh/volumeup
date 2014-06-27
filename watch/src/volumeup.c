/* Author: Shravan Aras (123.shravan@gmail.com)
 * Date: 6-25-2014
 */

#include <pebble.h>

/* Constants which represent different operations like
 * volume up / down / mute. */
#define DOWN 601
#define MUTE 602
#define UP 603

/* Our app only has one key for the volume. */
#define KEY 111

static Window *window;
static TextLayer *text_layer;

static void select_click_handler(ClickRecognizerRef recognizer, void *context) {
	/* Send the message to the phone. */
	DictionaryIterator *iter;
	app_message_outbox_begin(&iter);				
	Tuplet tuple = TupletInteger(KEY, MUTE);
	dict_write_tuplet(iter, &tuple);
	app_message_outbox_send();

  APP_LOG(APP_LOG_LEVEL_DEBUG, "Mute command sent");
}

static void up_click_handler(ClickRecognizerRef recognizer, void *context) {
	/* Send the message to the phone. */
	DictionaryIterator *iter;
	app_message_outbox_begin(&iter);				
	Tuplet tuple = TupletInteger(KEY, UP);
	dict_write_tuplet(iter, &tuple);
	app_message_outbox_send();

  APP_LOG(APP_LOG_LEVEL_DEBUG, "Up command sent");
}

static void down_click_handler(ClickRecognizerRef recognizer, void *context) {
	/* Send the message to the phone. */
	DictionaryIterator *iter;
	app_message_outbox_begin(&iter);				
	Tuplet tuple = TupletInteger(KEY, DOWN);
	dict_write_tuplet(iter, &tuple);
	app_message_outbox_send();

  APP_LOG(APP_LOG_LEVEL_DEBUG, "Down command sent");
}

static void click_config_provider(void *context) {
  window_single_click_subscribe(BUTTON_ID_SELECT, select_click_handler);
  window_single_click_subscribe(BUTTON_ID_UP, up_click_handler);
  window_single_click_subscribe(BUTTON_ID_DOWN, down_click_handler);
}

static void window_load(Window *window) {
  Layer *window_layer = window_get_root_layer(window);
  GRect bounds = layer_get_bounds(window_layer);

  text_layer = text_layer_create((GRect) { .origin = { 0, 32 }, .size = { bounds.size.w, 60 } });
  text_layer_set_text(text_layer, "Adjust the volume \n based on the \n up/down buttons");
  text_layer_set_text_alignment(text_layer, GTextAlignmentCenter);
  layer_add_child(window_layer, text_layer_get_layer(text_layer));
}

static void window_unload(Window *window) {
  text_layer_destroy(text_layer);
}

/* Callback handler for outgoing message success. */
void handler_out_ok(DictionaryIterator *sent, void *context){
}

/* Callback handler for outgoing message failure. */
void handler_out_fail(DictionaryIterator *fail, AppMessageResult reason,
															void *context){

}

/* Callback handler for incoming message accept. */
void handler_in_accept(DictionaryIterator *accept, void *context){

}

/* Callback handler for incoming message reject. */
void handler_in_reject(AppMessageResult reason, void *context){
	/* Maybe I am out of memory or something. */
}

static void init(void) {
	/* Init the callback functions for communicating with the phone. */
	app_message_register_inbox_received(handler_in_accept);
  app_message_register_inbox_dropped(handler_in_reject);
  app_message_register_outbox_sent(handler_out_ok);
  app_message_register_outbox_failed(handler_out_fail);

  window = window_create();
  window_set_click_config_provider(window, click_config_provider);
  window_set_window_handlers(window, (WindowHandlers) {
    .load = window_load,
    .unload = window_unload,
  });
  const bool animated = true;
  window_stack_push(window, animated);

	/* Set the inbound and outbound message size. */
  const uint32_t inbound_size = 64;
  const uint32_t outbound_size = 64;
  app_message_open(inbound_size, outbound_size);
}

static void deinit(void) {
  window_destroy(window);
}

int main(void) {
  init();

  APP_LOG(APP_LOG_LEVEL_DEBUG, "Done initializing, pushed window: %p", window);

  app_event_loop();
  deinit();
}
