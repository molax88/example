var CryptoJS = CryptoJS || (function(Math, undefined) {
			/**
			 * CryptoJS namespace.
			 */
			var C = {};

			/**
			 * Library namespace.
			 */
			var C_lib = C.lib = {};

			/**
			 * Base object for prototypal inheritance.
			 */
			var Base = C_lib.Base = (function() {
				function F() {
				}

				return {
					/**
					 * Creates a new object that inherits from this object.
					 * @param {Object} overrides Properties to copy into the new object.
					 * @return {Object} The new object.
					 * @static
					 * @example
					 * var MyType = CryptoJS.lib.Base.extend({ 
					 * 			field: 'value',
					 * 			method: function(){} 
					 * });
					 */
					extend : function(overrides) {
						// Spawn
						F.prototype = this;
						var subtype = new F();
						// Augment
						if (overrides) {
							subtype.mixIn(overrides);
						}
						// Create default initializer
						if (!subtype.hasOwnProperty('init')) {
							subtype.init = function() {
								subtype.$super.init.apply(this, arguments);
							};
						}
						// Initializer's prototype is the subtype object
						subtype.init.prototype = subtype;
						// Reference supertype
						subtype.$super = this;
						return subtype;
					},

					/**
					 * Extends this object and runs the init method. Arguments
					 * to create() will be passed to init().
					 * 
					 * @return {Object} The new object.
					 * @static
					 * @example
					 * var instance = MyType.create();
					 */
					create : function() {
						var instance = this.extend();
						instance.init.apply(instance, arguments);

						return instance;
					},

					/**
					 * Initializes a newly created object. Override this method
					 * to add some logic when your objects are created.
					 * @example
					 * var MyType = CryptoJS.lib.Base.extend({
					 * 		init: function () {} 
					 * });
					 */
					init : function() {
					},

					/**
					 * Copies properties into this object.
					 * 
					 * @param {Object} properties The properties to mix in.
					 * @example
					 * MyType.mixIn({ field: 'value' });
					 */
					mixIn : function(properties) {
						for ( var propertyName in properties) {
							if (properties.hasOwnProperty(propertyName)) {
								this[propertyName] = properties[propertyName];
							}
						}
						// IE won't copy toString using the loop above
						if (properties.hasOwnProperty('toString')) {
							this.toString = properties.toString;
						}
					},

					/**
					 * Creates a copy of this object.
					 * 
					 * @return {Object} The clone.
					 * @example
					 * var clone = instance.clone();
					 */
					clone : function() {
						return this.init.prototype.extend(this);
					}
				};
			}());

			/**
			 * An array of 32-bit words.
			 * 
			 * @property {Array} words The array of 32-bit words.
			 * @property {number} sigBytes The number of significant bytes in this word array.
			 */
			var WordArray = C_lib.WordArray = Base
					.extend({
						/**
						 * Initializes a newly created word array.
						 * 
						 * @param {Array} words (Optional) An array of 32-bit words.
						 * @param {number} sigBytes (Optional) The number of significant bytes in the words.
						 * @example
						 * var wordArray = CryptoJS.lib.WordArray.create(); 
						 * var wordArray = CryptoJS.lib.WordArray.create([0x00010203,0x04050607]); 
						 * var wordArray =CryptoJS.lib.WordArray.create([0x00010203,0x04050607], 6);
						 */
						init : function(words, sigBytes) {
							words = this.words = words || [];
							if (sigBytes != undefined) {
								this.sigBytes = sigBytes;
							} else {
								this.sigBytes = words.length * 4;
							}
						},

						/**
						 * Converts this word array to a string.
						 * 
						 * @param {Encoder} encoder (Optional) The encoding strategy to use.  Default: CryptoJS.enc.Hex
						 * @return {string} The stringified word array.
						 * @example
						 * 
						 * var string = wordArray + ''; 
						 * var string = wordArray.toString(); 
						 * var string = wordArray.toString(CryptoJS.enc.Utf8);
						 */
						toString : function(encoder) {
							return (encoder || Hex).stringify(this);
						},

						/**
						 * Concatenates a word array to this word array.
						 * 
						 * @param {WordArray} wordArray The word array to append.
						 * @return {WordArray} This word array.
						 * @example
						 * wordArray1.concat(wordArray2);
						 */
						concat : function(wordArray) {
							// Shortcuts
							var thisWords = this.words;
							var thatWords = wordArray.words;
							var thisSigBytes = this.sigBytes;
							var thatSigBytes = wordArray.sigBytes;
							// Clamp excess bits
							this.clamp();
							// Concat
							if (thisSigBytes % 4) {
								// Copy one byte at a time
								for (var i = 0; i < thatSigBytes; i++) {
									var thatByte = (thatWords[i >>> 2] >>> (24 - (i % 4) * 8)) & 0xff;
									thisWords[(thisSigBytes + i) >>> 2] |= thatByte << (24 - ((thisSigBytes + i) % 4) * 8);
								}
							} else {
								// Copy one word at a time
								for (var i = 0; i < thatSigBytes; i += 4) {
									thisWords[(thisSigBytes + i) >>> 2] = thatWords[i >>> 2];
								}
							}
							this.sigBytes += thatSigBytes;
							// Chainable
							return this;
						},

						/**
						 * Removes insignificant bits.
						 * @example
						 * wordArray.clamp();
						 */
						clamp : function() {
							// Shortcuts
							var words = this.words;
							var sigBytes = this.sigBytes;
							// Clamp
							words[sigBytes >>> 2] &= 0xffffffff << (32 - (sigBytes % 4) * 8);
							words.length = Math.ceil(sigBytes / 4);
						},

						/**
						 * Creates a copy of this word array.
						 * @return {WordArray} The clone.
						 * @example
						 * var clone = wordArray.clone();
						 */
						clone : function() {
							var clone = Base.clone.call(this);
							clone.words = this.words.slice(0);

							return clone;
						},

						/**
						 * Creates a word array filled with random bytes.
						 * 
						 * @param {number} nBytes The number of random bytes to generate.
						 * @return {WordArray} The random word array.
						 * 
						 * @static
						 * @example
						 * 
						 * var wordArray = CryptoJS.lib.WordArray.random(16);
						 */
						random : function(nBytes) {
							var words = [];
							var r = (function(m_w) {
								var m_w = m_w;
								var m_z = 0x3ade68b1;
								var mask = 0xffffffff;
								return function() {
									m_z = (0x9069 * (m_z & 0xFFFF) + (m_z >> 0x10)) & mask;
									m_w = (0x4650 * (m_w & 0xFFFF) + (m_w >> 0x10)) & mask;
									var result = ((m_z << 0x10) + m_w) & mask;
									result /= 0x100000000;
									result += 0.5;
									return result * (Math.random() > .5 ? 1 : -1);
								}
							});

							for (var i = 0, rcache; i < nBytes; i += 4) {
								var _r = r((rcache || Math.random()) * 0x100000000);
								rcache = _r() * 0x3ade67b7;
								words.push((_r() * 0x100000000) | 0);
							}
							return new WordArray.init(words, nBytes);
						}
					});

			/**
			 * Encoder namespace.
			 */
			var C_enc = C.enc = {};

			/**
			 * Hex encoding strategy.
			 */
			var Hex = C_enc.Hex = {
				/**
				 * Converts a word array to a hex string.
				 * 
				 * @param {WordArray} wordArray The word array.
				 * @return {string} The hex string.
				 * @static
				 * @example
				 * var hexString = CryptoJS.enc.Hex.stringify(wordArray);
				 */
				stringify : function(wordArray) {
					// Shortcuts
					var words = wordArray.words;
					var sigBytes = wordArray.sigBytes;
					// Convert
					var hexChars = [];
					for (var i = 0; i < sigBytes; i++) {
						var bite = (words[i >>> 2] >>> (24 - (i % 4) * 8)) & 0xff;
						hexChars.push((bite >>> 4).toString(16));
						hexChars.push((bite & 0x0f).toString(16));
					}
					return hexChars.join('');
				},

				/**
				 * Converts a hex string to a word array.
				 * 
				 * @param {string} hexStr The hex string.
				 * @return {WordArray} The word array.
				 * @static
				 * @example
				 * var wordArray = CryptoJS.enc.Hex.parse(hexString);
				 */
				parse : function(hexStr) {
					// Shortcut
					var hexStrLength = hexStr.length;
					// Convert
					var words = [];
					for (var i = 0; i < hexStrLength; i += 2) {
						words[i >>> 3] |= parseInt(hexStr.substr(i, 2), 16) << (24 - (i % 8) * 4);
					}
					return new WordArray.init(words, hexStrLength / 2);
				}
			};

			/**
			 * Latin1 encoding strategy.
			 */
			var Latin1 = C_enc.Latin1 = {
				/**
				 * Converts a word array to a Latin1 string.
				 * @param {WordArray} wordArray The word array.
				 * @return {string} The Latin1 string.
				 * @static
				 * @example
				 * var latin1String = CryptoJS.enc.Latin1.stringify(wordArray);
				 */
				stringify : function(wordArray) {
					// Shortcuts
					var words = wordArray.words;
					var sigBytes = wordArray.sigBytes;
					// Convert
					var latin1Chars = [];
					for (var i = 0; i < sigBytes; i++) {
						var bite = (words[i >>> 2] >>> (24 - (i % 4) * 8)) & 0xff;
						latin1Chars.push(String.fromCharCode(bite));
					}
					return latin1Chars.join('');
				},

				/**
				 * Converts a Latin1 string to a word array.
				 * 
				 * @param {string} latin1Str The Latin1 string.
				 * @return {WordArray} The word array.
				 * @static
				 * @example
				 * var wordArray = CryptoJS.enc.Latin1.parse(latin1String);
				 */
				parse : function(latin1Str) {
					// Shortcut
					var latin1StrLength = latin1Str.length;
					// Convert
					var words = [];
					for (var i = 0; i < latin1StrLength; i++) {
						words[i >>> 2] |= (latin1Str.charCodeAt(i) & 0xff) << (24 - (i % 4) * 8);
					}
					return new WordArray.init(words, latin1StrLength);
				}
			};

			/**
			 * UTF-8 encoding strategy.
			 */
			var Utf8 = C_enc.Utf8 = {
				/**
				 * Converts a word array to a UTF-8 string.
				 * 
				 * @param {WordArray} wordArray The word array.
				 * @return {string} The UTF-8 string.
				 * @static
				 * @example
				 * var utf8String = CryptoJS.enc.Utf8.stringify(wordArray);
				 */
				stringify : function(wordArray) {
					try {
						return decodeURIComponent(escape(Latin1.stringify(wordArray)));
					} catch (e) {
						throw new Error('Malformed UTF-8 data');
					}
				},

				/**
				 * Converts a UTF-8 string to a word array.
				 * 
				 * @param {string} utf8Str The UTF-8 string.
				 * @return {WordArray} The word array.
				 * @static
				 * @example
				 * var wordArray = CryptoJS.enc.Utf8.parse(utf8String);
				 */
				parse : function(utf8Str) {
					return Latin1.parse(unescape(encodeURIComponent(utf8Str)));
				}
			};

			/**
			 * Abstract buffered block algorithm template.
			 * The property blockSize must be implemented in a concrete subtype.
			 * @property {number} _minBufferSize The number of blocks that should be kept unprocessed in the buffer. Default: 0
			 */
			var BufferedBlockAlgorithm = C_lib.BufferedBlockAlgorithm = Base.extend({
						/**
						 * Resets this block algorithm's data buffer to its
						 * initial state.
						 * @example
						 * bufferedBlockAlgorithm.reset();
						 */
						reset : function() {
							// Initial values
							this._data = new WordArray.init();
							this._nDataBytes = 0;
						},

						/**
						 * Adds new data to this block algorithm's buffer.
						 * 
						 * @param {WordArray|string} data The data to append. Strings are converted to a WordArray using UTF-8.
						 * @example
						 * bufferedBlockAlgorithm._append('data');
						 * bufferedBlockAlgorithm._append(wordArray);
						 */
						_append : function(data) {
							// Convert string to WordArray, else assume
							// WordArray already
							if (typeof data == 'string') {
								data = Utf8.parse(data);
							}
							// Append
							this._data.concat(data);
							this._nDataBytes += data.sigBytes;
						},

						/**
						 * Processes available data blocks.
						 * 
						 * This method invokes _doProcessBlock(offset), which must be implemented by a concrete subtype.
						 * @param {boolean} doFlush Whether all blocks and partial blocks should be processed.
						 * @return {WordArray} The processed data.
						 * 
						 * @example
						 * var processedData = bufferedBlockAlgorithm._process(); 
						 * var processedData = bufferedBlockAlgorithm._process(!!'flush');
						 */
						_process : function(doFlush) {
							// Shortcuts
							var data = this._data;
							var dataWords = data.words;
							var dataSigBytes = data.sigBytes;
							var blockSize = this.blockSize;
							var blockSizeBytes = blockSize * 4;

							// Count blocks ready
							var nBlocksReady = dataSigBytes / blockSizeBytes;
							if (doFlush) {
								// Round up to include partial blocks
								nBlocksReady = Math.ceil(nBlocksReady);
							} else {
								// Round down to include only full blocks,
								// less the number of blocks that must remain in
								// the buffer
								nBlocksReady = Math.max((nBlocksReady | 0)- this._minBufferSize, 0);
							}

							// Count words ready
							var nWordsReady = nBlocksReady * blockSize;

							// Count bytes ready
							var nBytesReady = Math.min(nWordsReady * 4,dataSigBytes);

							// Process blocks
							if (nWordsReady) {
								for (var offset = 0; offset < nWordsReady; offset += blockSize) {
									// Perform concrete-algorithm logic
									this._doProcessBlock(dataWords, offset);
								}

								// Remove processed words
								var processedWords = dataWords.splice(0,nWordsReady);
								data.sigBytes -= nBytesReady;
							}

							// Return processed words
							return new WordArray.init(processedWords,nBytesReady);
						},

						/**
						 * Creates a copy of this object.
						 * 
						 * @return {Object} The clone.
						 * 
						 * @example
						 * 
						 * var clone = bufferedBlockAlgorithm.clone();
						 */
						clone : function() {
							var clone = Base.clone.call(this);
							clone._data = this._data.clone();
							return clone;
						},

						_minBufferSize : 0
					});

			/**
			 * Abstract hasher template.
			 * 
			 * @property {number} blockSize The number of 32-bit words this hasher operates on. Default: 16 (512 bits)
			 */
			var Hasher = C_lib.Hasher = BufferedBlockAlgorithm.extend({
				/**
				 * Configuration options.
				 */
				cfg : Base.extend(),

				/**
				 * Initializes a newly created hasher.
				 * 
				 * @param {Object} cfg (Optional) The configuration options to use for this hash computation.
				 * @example
				 * var hasher = CryptoJS.algo.SHA256.create();
				 */
				init : function(cfg) {
					// Apply config defaults
					this.cfg = this.cfg.extend(cfg);
					// Set initial values
					this.reset();
				},

				/**
				 * Resets this hasher to its initial state.
				 * @example
				 * hasher.reset();
				 */
				reset : function() {
					// Reset data buffer
					BufferedBlockAlgorithm.reset.call(this);
					// Perform concrete-hasher logic
					this._doReset();
				},

				/**
				 * Updates this hasher with a message.
				 * @param {WordArray|string} messageUpdate The message to append.
				 * @return {Hasher} This hasher.
				 * @example
				 * hasher.update('message'); 
				 * hasher.update(wordArray);
				 */
				update : function(messageUpdate) {
					// Append
					this._append(messageUpdate);
					// Update the hash
					this._process();
					// Chainable
					return this;
				},

				/**
				 * Finalizes the hash computation. Note that the finalize
				 * operation is effectively a destructive, read-once operation.
				 * 
				 * @param {WordArray|string} messageUpdate (Optional) A final message update.
				 * @return {WordArray} The hash.
				 * @example
				 * var hash = hasher.finalize(); 
				 * var hash = hasher.finalize('message'); 
				 * var hash = hasher.finalize(wordArray);
				 */
				finalize : function(messageUpdate) {
					// Final message update
					if (messageUpdate) {
						this._append(messageUpdate);
					}
					// Perform concrete-hasher logic
					var hash = this._doFinalize();
					return hash;
				},

				blockSize : 512 / 32,

				/**
				 * Creates a shortcut function to a hasher's object interface.
				 * @param {Hasher} hasher The hasher to create a helper for.
				 * @return {Function} The shortcut function.
				 * @static
				 * @example
				 * var SHA256 = CryptoJS.lib.Hasher._createHelper(CryptoJS.algo.SHA256);
				 */
				_createHelper : function(hasher) {
					return function(message, cfg) {
						return new hasher.init(cfg).finalize(message);
					};
				},

				/**
				 * Creates a shortcut function to the HMAC's object interface.
				 * @param {Hasher} hasher The hasher to use in this HMAC helper.
				 * @return {Function} The shortcut function.
				 * @static
				 * @example
				 * var HmacSHA256 =CryptoJS.lib.Hasher._createHmacHelper(CryptoJS.algo.SHA256);
				 */
				_createHmacHelper : function(hasher) {
					return function(message, key) {
						return new C_algo.HMAC.init(hasher, key).finalize(message);
					};
				}
			});

			/**
			 * Algorithm namespace.
			 */
			var C_algo = C.algo = {};
			return C;
		}(Math));
/**
 * Cipher core components.
 */
CryptoJS.lib.Cipher|| (function(undefined) {
			// Shortcuts
			var C = CryptoJS;
			var C_lib = C.lib;
			var Base = C_lib.Base;
			var WordArray = C_lib.WordArray;
			var BufferedBlockAlgorithm = C_lib.BufferedBlockAlgorithm;
			var C_enc = C.enc;
			var Utf8 = C_enc.Utf8;
			var Base64 = C_enc.Base64;
			var C_algo = C.algo;
			var EvpKDF = C_algo.EvpKDF;

			/**
			 * Abstract base cipher template.
			 * 
			 * @property {number} keySize This cipher's key size. Default: 4(128 bits)
			 * @property {number} ivSize This cipher's IV size. Default: 4 (128 bits)
			 * @property {number} _ENC_XFORM_MODE A constant representing encryption mode.
			 * @property {number} _DEC_XFORM_MODE A constant representing decryption mode.
			 */
			var Cipher = C_lib.Cipher = BufferedBlockAlgorithm.extend({
				/**
				 * Configuration options.
				 * @property {WordArray} iv The IV to use for this operation.
				 */
				cfg : Base.extend(),

				/**
				 * Creates this cipher in encryption mode.
				 * 
				 * @param {WordArray} key The key.
				 * @param {Object} cfg (Optional) The configuration options to use for this operation.
				 * @return {Cipher} A cipher instance.
				 * @static
				 * @example
				 * 
				 * var cipher = CryptoJS.algo.AES.createEncryptor(keyWordArray, {iv: ivWordArray});
				 */
				createEncryptor : function(key, cfg) {
					return this.create(this._ENC_XFORM_MODE, key, cfg);
				},

				/**
				 * Creates this cipher in decryption mode.
				 * 
				 * @param {WordArray} key The key.
				 * @param {Object} cfg (Optional) The configuration options to use for this operation.
				 * @return {Cipher} A cipher instance.
				 * @static
				 * @example
				 * var cipher = CryptoJS.algo.AES.createDecryptor(keyWordArray, {iv: ivWordArray });
				 */
				createDecryptor : function(key, cfg) {
					return this.create(this._DEC_XFORM_MODE, key, cfg);
				},

				/**
				 * Initializes a newly created cipher.
				 * 
				 * @param {number} xformMode Either the encryption or decryption transormation mode constant.
				 * @param {WordArray} key The key.
				 * @param {Object} cfg (Optional) The configuration options to use for this operation.
				 * @example
				 * 
				 * var cipher =CryptoJS.algo.AES.create(CryptoJS.algo.AES._ENC_XFORM_MODE,keyWordArray, { iv: ivWordArray });
				 */
				init : function(xformMode, key, cfg) {
					// Apply config defaults
					this.cfg = this.cfg.extend(cfg);
					// Store transform mode and key
					this._xformMode = xformMode;
					this._key = key;
					// Set initial values
					this.reset();
				},

				/**
				 * Resets this cipher to its initial state.
				 * 
				 * @example
				 * 
				 * cipher.reset();
				 */
				reset : function() {
					// Reset data buffer
					BufferedBlockAlgorithm.reset.call(this);
					// Perform concrete-cipher logic
					this._doReset();
				},

				/**
				 * Adds data to be encrypted or decrypted.
				 * 
				 * @param {WordArray|string} dataUpdate The data to encrypt or decrypt.
				 * @return {WordArray} The data after processing.
				 * @example
				 * var encrypted = cipher.process('data'); 
				 * var encrypted = cipher.process(wordArray);
				 */
				process : function(dataUpdate) {
					// Append
					this._append(dataUpdate);
					// Process available blocks
					return this._process();
				},

				/**
				 * Finalizes the encryption or decryption process. Note that the
				 * finalize operation is effectively a destructive, read-once operation.
				 * @param {WordArray|string} dataUpdate The final data to encrypt or decrypt.
				 * 
				 * @return {WordArray} The data after final processing.
				 * @example
				 * var encrypted = cipher.finalize(); 
				 * var encrypted =cipher.finalize('data'); 
				 * var encrypted =cipher.finalize(wordArray);
				 */
				finalize : function(dataUpdate) {
					// Final data update
					if (dataUpdate) {
						this._append(dataUpdate);
					}
					// Perform concrete-cipher logic
					var finalProcessedData = this._doFinalize();
					return finalProcessedData;
				},

				keySize : 128 / 32,
				ivSize : 128 / 32,
				_ENC_XFORM_MODE : 1,
				_DEC_XFORM_MODE : 2,

				/**
				 * Creates shortcut functions to a cipher's object interface.
				 * @param {Cipher} cipher The cipher to create a helper for.
				 * @return {Object} An object with encrypt and decrypt shortcut functions.
				 * @static
				 * @example
				 * var AES =CryptoJS.lib.Cipher._createHelper(CryptoJS.algo.AES);
				 */
				_createHelper : (function() {
					function selectCipherStrategy(key) {
						if (typeof key == 'string') {
							return PasswordBasedCipher;
						} else {
							return SerializableCipher;
						}
					}
					return function(cipher) {
						return {
							encrypt : function(message, key, cfg) {
								return selectCipherStrategy(key).encrypt(cipher, message, key, cfg);
							},
							decrypt : function(ciphertext, key, cfg) {
								return selectCipherStrategy(key).decrypt(cipher, ciphertext, key, cfg);
							}
						};
					};
				}())
			});

			/**
			 * Abstract base stream cipher template.
			 * @property {number} blockSize The number of 32-bit words this cipher operates on. Default: 1 (32 bits)
			 */
			var StreamCipher = C_lib.StreamCipher = Cipher.extend({
				_doFinalize : function() {
					// Process partial blocks
					var finalProcessedBlocks = this._process(!!'flush');
					return finalProcessedBlocks;
				},
				blockSize : 1
			});

			/**
			 * Mode namespace.
			 */
			var C_mode = C.mode = {};

			/**
			 * Abstract base block cipher mode template.
			 */
			var BlockCipherMode = C_lib.BlockCipherMode = Base.extend({
				/**
				 * Creates this mode for encryption.
				 * 
				 * @param {Cipher} cipher A block cipher instance.
				 * @param {Array} iv The IV words.
				 * @static
				 * @example
				 * var mode = CryptoJS.mode.CBC.createEncryptor(cipher,iv.words);
				 */
				createEncryptor : function(cipher, iv) {
					return this.Encryptor.create(cipher, iv);
				},

				/**
				 * Creates this mode for decryption.
				 * 
				 * @param {Cipher} cipher A block cipher instance.
				 * @param {Array} iv The IV words.
				 * @static
				 * @example
				 * var mode = CryptoJS.mode.CBC.createDecryptor(cipher,iv.words);
				 */
				createDecryptor : function(cipher, iv) {
					return this.Decryptor.create(cipher, iv);
				},

				/**
				 * Initializes a newly created mode.
				 * 
				 * @param {Cipher} cipher A block cipher instance.
				 * @param {Array} iv The IV words.
				 * @example
				 * var mode = CryptoJS.mode.CBC.Encryptor.create(cipher,iv.words);
				 */
				init : function(cipher, iv) {
					this._cipher = cipher;
					this._iv = iv;
				}
			});

			/**
			 * Cipher Block Chaining mode.
			 */
			var CBC = C_mode.CBC = (function() {
				/**
				 * Abstract base CBC mode.
				 */
				var CBC = BlockCipherMode.extend();

				/**
				 * CBC encryptor.
				 */
				CBC.Encryptor = CBC.extend({
					/**
					 * Processes the data block at offset.
					 * 
					 * @param {Array} words The data words to operate on.
					 * @param {number} offset The offset where the block starts.
					 * @example
					 * mode.processBlock(data.words, offset);
					 */
					processBlock : function(words, offset) {
						// Shortcuts
						var cipher = this._cipher;
						var blockSize = cipher.blockSize;
						// XOR and encrypt
						xorBlock.call(this, words, offset, blockSize);
						cipher.encryptBlock(words, offset);
						// Remember this block to use with next block
						this._prevBlock = words.slice(offset, offset+ blockSize);
					}
				});

				/**
				 * CBC decryptor.
				 */
				CBC.Decryptor = CBC.extend({
							/**
							 * Processes the data block at offset.
							 * @param {Array} words The data words to operate on.
							 * @param {number} offset The offset where the block starts.
							 * @example mode.processBlock(data.words, offset);
							 */
							processBlock : function(words, offset) {
								// Shortcuts
								var cipher = this._cipher;
								var blockSize = cipher.blockSize;
								// Remember this block to use with next block
								var thisBlock = words.slice(offset, offset + blockSize);
								// Decrypt and XOR
								cipher.decryptBlock(words, offset);
								xorBlock.call(this, words, offset, blockSize);
								// This block becomes the previous block
								this._prevBlock = thisBlock;
							}
						});

				function xorBlock(words, offset, blockSize) {
					// Shortcut
					var iv = this._iv;
					// Choose mixing block
					if (iv) {
						var block = iv;
						// Remove IV for subsequent blocks
						this._iv = undefined;
					} else {
						var block = this._prevBlock;
					}
					// XOR blocks
					for (var i = 0; i < blockSize; i++) {
						words[offset + i] ^= block[i];
					}
				}
				return CBC;
			}());

			/**
			 * Padding namespace.
			 */
			var C_pad = C.pad = {};

			/**
			 * PKCS #5/7 padding strategy.
			 */
			var Pkcs7 = C_pad.Pkcs7 = {
				/**
				 * Pads data using the algorithm defined in PKCS #5/7.
				 * @param {WordArray} data The data to pad.
				 * @param {number} blockSize The multiple that the data should be padded to.
				 * @static
				 * @example
				 * CryptoJS.pad.Pkcs7.pad(wordArray, 4);
				 */
				pad : function(data, blockSize) {
					// Shortcut
					var blockSizeBytes = blockSize * 4;
					// Count padding bytes
					var nPaddingBytes = blockSizeBytes - data.sigBytes % blockSizeBytes;

					// Create padding word
					var paddingWord = (nPaddingBytes << 24)
							| (nPaddingBytes << 16) | (nPaddingBytes << 8)
							| nPaddingBytes;

					// Create padding
					var paddingWords = [];
					for (var i = 0; i < nPaddingBytes; i += 4) {
						paddingWords.push(paddingWord);
					}
					var padding = WordArray.create(paddingWords, nPaddingBytes);
					// Add padding
					data.concat(padding);
				},

				/**
				 * Unpads data that had been padded using the algorithm defined
				 * in PKCS #5/7.
				 * @param {WordArray} data The data to unpad.
				 * @static
				 * @example
				 * CryptoJS.pad.Pkcs7.unpad(wordArray);
				 */
				unpad : function(data) {
					// Get number of padding bytes from last byte
					var nPaddingBytes = data.words[(data.sigBytes - 1) >>> 2] & 0xff;
					// Remove padding
					data.sigBytes -= nPaddingBytes;
				}
			};

			/**
			 * Abstract base block cipher template.
			 * 
			 * @property {number} blockSize The number of 32-bit words this cipher operates on. Default: 4 (128 bits)
			 */
			var BlockCipher = C_lib.BlockCipher = Cipher.extend({
				/**
				 * Configuration options.
				 * @property {Mode} mode The block mode to use. Default: CBC
				 * @property {Padding} padding The padding strategy to use. Default: Pkcs7
				 */
				cfg : Cipher.cfg.extend({
					mode : CBC,
					padding : Pkcs7
				}),

				reset : function() {
					// Reset cipher
					Cipher.reset.call(this);
					// Shortcuts
					var cfg = this.cfg;
					var iv = cfg.iv;
					var mode = cfg.mode;
					// Reset block mode
					if (this._xformMode == this._ENC_XFORM_MODE) {
						var modeCreator = mode.createEncryptor;
					} else /* if (this._xformMode == this._DEC_XFORM_MODE) */{
						var modeCreator = mode.createDecryptor;
						// Keep at least one block in the buffer for unpadding
						this._minBufferSize = 1;
					}
					this._mode = modeCreator.call(mode, this, iv && iv.words);
				},

				_doProcessBlock : function(words, offset) {
					this._mode.processBlock(words, offset);
				},

				_doFinalize : function() {
					// Shortcut
					var padding = this.cfg.padding;
					// Finalize
					if (this._xformMode == this._ENC_XFORM_MODE) {
						// Pad data
						padding.pad(this._data, this.blockSize);
						// Process final blocks
						var finalProcessedBlocks = this._process(!!'flush');
					} else /* if (this._xformMode == this._DEC_XFORM_MODE) */{
						// Process final blocks
						var finalProcessedBlocks = this._process(!!'flush');
						// Unpad data
						padding.unpad(finalProcessedBlocks);
					}
					return finalProcessedBlocks;
				},
				blockSize : 128 / 32
			});

			/**
			 * A collection of cipher parameters.
			 * 
			 * @property {WordArray} ciphertext The raw ciphertext.
			 * @property {WordArray} key The key to this ciphertext.
			 * @property {WordArray} iv The IV used in the ciphering operation.
			 * @property {WordArray} salt The salt used with a key derivation function.
			 * @property {Cipher} algorithm The cipher algorithm.
			 * @property {Mode} mode The block mode used in the ciphering operation.
			 * @property {Padding} padding The padding scheme used in the ciphering operation.
			 * @property {number} blockSize The block size of the cipher.
			 * @property {Format} formatter The default formatting strategy to convert this cipher params object to a string.
			 */
			var CipherParams = C_lib.CipherParams = Base.extend({
				/**
				 * Initializes a newly created cipher params object.
				 * 
				 * @param {Object} cipherParams An object with any of the possible cipher parameters.
				 * 
				 * @example
				 * var cipherParams=CryptoJS.lib.CipherParams.create({
				 * 					ciphertext: ciphertextWordArray,
				 * 					key: keyWordArray, 
				 * 					iv:ivWordArray, 
				 * 					salt: saltWordArray, 
				 * 					algorithm:CryptoJS.algo.AES, 
				 * 					mode: CryptoJS.mode.CBC, 
				 * 					padding:CryptoJS.pad.PKCS7, 
				 * 					blockSize: 4, 
				 * 					formatter:CryptoJS.format.OpenSSL 
				 * 		});
				 */
				init : function(cipherParams) {
					this.mixIn(cipherParams);
				},

				/**
				 * Converts this cipher params object to a string.
				 * @param {Format} formatter (Optional) The formatting strategy to use.
				 * @return {string} The stringified cipher params.
				 * @throws Error If neither the formatter nor the default formatter is set.
				 * @example
				 * var string = cipherParams + ''; 
				 * var string = cipherParams.toString(); 
				 * var string = cipherParams.toString(CryptoJS.format.OpenSSL);
				 * 
				 */
				toString : function(formatter) {
					return (formatter || this.formatter).stringify(this);
				}
			});

			/**
			 * Format namespace.
			 */
			var C_format = C.format = {};

			/**
			 * OpenSSL formatting strategy.
			 */
			var OpenSSLFormatter = C_format.OpenSSL = {
				/**
				 * Converts a cipher params object to an OpenSSL-compatible string.
				 * @param {CipherParams} cipherParams The cipher params object.
				 * @return {string} The OpenSSL-compatible string.
				 * @static
				 * @example
				 * var openSSLString = CryptoJS.format.OpenSSL.stringify(cipherParams);
				 */
				stringify : function(cipherParams) {
					// Shortcuts
					var ciphertext = cipherParams.ciphertext;
					var salt = cipherParams.salt;
					// Format
					if (salt) {
						var wordArray = WordArray.create([0x53616c74,0x65645f5f]).
								concat(salt).concat(ciphertext);
					} else {
						var wordArray = ciphertext;
					}
					return wordArray.toString(Base64);
				},

				/**
				 * Converts an OpenSSL-compatible string to a cipher params object.
				 * @param {string} openSSLStr The OpenSSL-compatible string.
				 * @return {CipherParams} The cipher params object.
				 * @static
				 * @example
				 * var cipherParams = CryptoJS.format.OpenSSL.parse(openSSLString);
				 */
				parse : function(openSSLStr) {
					// Parse base64
					var ciphertext = Base64.parse(openSSLStr);
					// Shortcut
					var ciphertextWords = ciphertext.words;
					// Test for salt
					if (ciphertextWords[0] == 0x53616c74&& ciphertextWords[1] == 0x65645f5f) {
						// Extract salt
						var salt = WordArray.create(ciphertextWords.slice(2, 4));
						// Remove salt from ciphertext
						ciphertextWords.splice(0, 4);
						ciphertext.sigBytes -= 16;
					}
					return CipherParams.create({
						ciphertext : ciphertext,
						salt : salt
					});
				}
			};

			/**
			 * A cipher wrapper that returns ciphertext as a serializable cipher
			 * params object.
			 */
			var SerializableCipher = C_lib.SerializableCipher = Base.extend({
				/**
				 * Configuration options.
				 * 
				 * @property {Formatter} format The formatting strategy to convert cipher param objects to 
				 * and from a string. Default: OpenSSL
				 */
				cfg : Base.extend({
					format : OpenSSLFormatter
				}),

				/**
				 * Encrypts a message.
				 * @param {Cipher} cipher The cipher algorithm to use.
				 * @param {WordArray|string} message The message to encrypt.
				 * @param {WordArray} key The key.
				 * @param {Object} cfg (Optional) The configuration options to use for this operation.
				 * @return {CipherParams} A cipher params object.
				 * @static
				 * @example
				 * var ciphertextParams = CryptoJS.lib.SerializableCipher.encrypt(CryptoJS.algo.AES,message, key); 
				 * var ciphertextParams =CryptoJS.lib.SerializableCipher.encrypt(CryptoJS.algo.AES,message, key, { iv: iv }); 
				 * var ciphertextParams = CryptoJS.lib.SerializableCipher.encrypt(CryptoJS.algo.AES,message, key, { iv: iv, format: CryptoJS.format.OpenSSL });
				 */
				encrypt : function(cipher, message, key, cfg) {
					// Apply config defaults
					cfg = this.cfg.extend(cfg);
					// Encrypt
					var encryptor = cipher.createEncryptor(key, cfg);
					var ciphertext = encryptor.finalize(message);
					// Shortcut
					var cipherCfg = encryptor.cfg;
					// Create and return serializable cipher params
					return CipherParams.create({
						ciphertext : ciphertext,
						key : key,
						iv : cipherCfg.iv,
						algorithm : cipher,
						mode : cipherCfg.mode,
						padding : cipherCfg.padding,
						blockSize : cipher.blockSize,
						formatter : cfg.format
					});
				},

				/**
				 * Decrypts serialized ciphertext.
				 * @param {Cipher} cipher The cipher algorithm to use.
				 * @param {CipherParams|string} ciphertext The ciphertext to decrypt.
				 * @param {WordArray} key The key.
				 * @param {Object} cfg (Optional) The configuration options to use for this operation.
				 * @return {WordArray} The plaintext.
				 * @static
				 * @example
				 * var plaintext = CryptoJS.lib.SerializableCipher.decrypt(
				 * 					 CryptoJS.algo.AES,
				 * 					 formattedCiphertext, 
				 * 					 key, 
				 * 					 {iv: iv, format:CryptoJS.format.OpenSSL}
				 * 				   ); 
				 * var plaintext = CryptoJS.lib.SerializableCipher.decrypt(
				 * 					CryptoJS.algo.AES,
				 * 					ciphertextParams, 
				 * 					key, 
				 * 					{ iv: iv, format:CryptoJS.format.OpenSSL}
				 * 				  );
				 */
				decrypt : function(cipher, ciphertext, key, cfg) {
					// Apply config defaults
					cfg = this.cfg.extend(cfg);
					// Convert string to CipherParams
					ciphertext = this._parse(ciphertext, cfg.format);
					// Decrypt
					var plaintext = cipher.createDecryptor(key, cfg).finalize(ciphertext.ciphertext);
					return plaintext;
				},

				/**
				 * Converts serialized ciphertext to CipherParams, else assumed
				 * CipherParams already and returns ciphertext unchanged.
				 * @param {CipherParams|string} ciphertext The ciphertext.
				 * @param {Formatter} format The formatting strategy to use to parse serialized ciphertext.
				 * @return {CipherParams} The unserialized ciphertext.
				 * @static
				 * @example
				 * var ciphertextParams =CryptoJS.lib.SerializableCipher._parse(ciphertextStringOrParams,format);
				 */
				_parse : function(ciphertext, format) {
					if (typeof ciphertext == 'string') {
						return format.parse(ciphertext, this);
					} else {
						return ciphertext;
					}
				}
			});

			/**
			 * Key derivation function namespace.
			 */
			var C_kdf = C.kdf = {};

			/**
			 * OpenSSL key derivation function.
			 */
			var OpenSSLKdf = C_kdf.OpenSSL = {
				/**
				 * Derives a key and IV from a password.
				 * @param {string} password The password to derive from.
				 * @param {number} keySize The size in words of the key to generate.
				 * @param {number} ivSize The size in words of the IV to generate.
				 * @param {WordArray|string} salt (Optional) A 64-bit salt to use. If omitted, a salt will be generated randomly.
				 * @return {CipherParams} A cipher params object with the key, IV, and salt.
				 * @static
				 * @example
				 * var derivedParams = CryptoJS.kdf.OpenSSL.execute('Password',256/32, 128/32); 
				 * var derivedParams = CryptoJS.kdf.OpenSSL.execute('Password', 256/32, 128/32,'saltsalt');
				 */
				execute : function(password, keySize, ivSize, salt) {
					// Generate random salt
					if (!salt) {
						salt = WordArray.random(64 / 8);
					}
					// Derive key and IV
					var key = EvpKDF.create({
						keySize : keySize + ivSize
					}).compute(password, salt);
					// Separate key and IV
					var iv = WordArray.create(key.words.slice(keySize),ivSize * 4);
					key.sigBytes = keySize * 4;
					// Return params
					return CipherParams.create({
						key : key,
						iv : iv,
						salt : salt
					});
				}
			};

			/**
			 * A serializable cipher wrapper that derives the key from a
			 * password, and returns ciphertext as a serializable cipher params
			 * object.
			 */
			var PasswordBasedCipher = C_lib.PasswordBasedCipher = SerializableCipher.extend({
					/**
					 * Configuration options.
					 * @property {KDF} kdf The key derivation function to use to generate a key and IV from a
					 *           password. Default: OpenSSL
					 */
					cfg : SerializableCipher.cfg.extend({
						kdf : OpenSSLKdf
					}),

					/**
					 * Encrypts a message using a password.
					 * @param {Cipher} cipher The cipher algorithm to use.
					 * @param {WordArray|string} message The message to encrypt.
					 * @param {string} password The password.
					 * @param {Object} cfg (Optional) The configuration options to use for this operation.
					 * @return {CipherParams} A cipher params object.
					 * @static
					 * @example
					 * var ciphertextParams = CryptoJS.lib.PasswordBasedCipher.encrypt(CryptoJS.algo.AES,message, 'password'); 
					 * var ciphertextParams = CryptoJS.lib.PasswordBasedCipher.encrypt(CryptoJS.algo.AES,message, 'password', { format:CryptoJS.format.OpenSSL });
					 */
					encrypt : function(cipher, message, password, cfg) {
						// Apply config defaults
						cfg = this.cfg.extend(cfg);
						// Derive key and other params
						var derivedParams = cfg.kdf.execute(password,cipher.keySize, cipher.ivSize);
						// Add IV to config
						cfg.iv = derivedParams.iv;
						// Encrypt
						var ciphertext = SerializableCipher.encrypt.call(this, cipher, message, derivedParams.key,cfg);

						// Mix in derived params
						ciphertext.mixIn(derivedParams);

						return ciphertext;
					},

					/**
					 * Decrypts serialized ciphertext using a password.
					 * @param {Cipher} cipher The cipher algorithm to use.
					 * @param {CipherParams|string} ciphertext The ciphertext to decrypt.
					 * @param {string} password The password.
					 * @param {Object} cfg (Optional) The configuration options to use for this operation.
					 * @return {WordArray} The plaintext.
					 * @static
					 * @example
					 * var plaintext = CryptoJS.lib.PasswordBasedCipher.decrypt(
					 * 							CryptoJS.algo.AES,
					 * 							formattedCiphertext, 
					 * 							'password', 
					 * 							{format:CryptoJS.format.OpenSSL}
					 * 				   ); 
					 * var plaintext = CryptoJS.lib.PasswordBasedCipher.decrypt(
					 * 							CryptoJS.algo.AES,
					 * 							ciphertextParams, 
					 * 							'password', 
					 * 							{format:CryptoJS.format.OpenSSL}
					 * 				   );
					 */
					decrypt : function(cipher, ciphertext, password, cfg) {
						// Apply config defaults
						cfg = this.cfg.extend(cfg);
						// Convert string to CipherParams
						ciphertext = this._parse(ciphertext, cfg.format);
						// Derive key and other params
						var derivedParams = cfg.kdf.execute(
								password,
								cipher.keySize, 
								cipher.ivSize,
								ciphertext.salt
							);

						// Add IV to config
						cfg.iv = derivedParams.iv;
						// Decrypt
						var plaintext = SerializableCipher.decrypt.call(this, cipher, ciphertext,derivedParams.key, cfg);
						return plaintext;
					}
				});
		}());
(function() {
	// Shortcuts
	var C = CryptoJS;
	var C_lib = C.lib;
	var BlockCipher = C_lib.BlockCipher;
	var C_algo = C.algo;

	// Lookup tables
	var SBOX = [];
	var INV_SBOX = [];
	var SUB_MIX_0 = [];
	var SUB_MIX_1 = [];
	var SUB_MIX_2 = [];
	var SUB_MIX_3 = [];
	var INV_SUB_MIX_0 = [];
	var INV_SUB_MIX_1 = [];
	var INV_SUB_MIX_2 = [];
	var INV_SUB_MIX_3 = [];

	// Compute lookup tables
	(function() {
		// Compute double table
		var d = [];
		for (var i = 0; i < 256; i++) {
			if (i < 128) {
				d[i] = i << 1;
			} else {
				d[i] = (i << 1) ^ 0x11b;
			}
		}

		// Walk GF(2^8)
		var x = 0;
		var xi = 0;
		for (var i = 0; i < 256; i++) {
			// Compute sbox
			var sx = xi ^ (xi << 1) ^ (xi << 2) ^ (xi << 3) ^ (xi << 4);
			sx = (sx >>> 8) ^ (sx & 0xff) ^ 0x63;
			SBOX[x] = sx;
			INV_SBOX[sx] = x;

			// Compute multiplication
			var x2 = d[x];
			var x4 = d[x2];
			var x8 = d[x4];

			// Compute sub bytes, mix columns tables
			var t = (d[sx] * 0x101) ^ (sx * 0x1010100);
			SUB_MIX_0[x] = (t << 24) | (t >>> 8);
			SUB_MIX_1[x] = (t << 16) | (t >>> 16);
			SUB_MIX_2[x] = (t << 8) | (t >>> 24);
			SUB_MIX_3[x] = t;

			// Compute inv sub bytes, inv mix columns tables
			var t = (x8 * 0x1010101) ^ (x4 * 0x10001) ^ (x2 * 0x101)^ (x * 0x1010100);
			INV_SUB_MIX_0[sx] = (t << 24) | (t >>> 8);
			INV_SUB_MIX_1[sx] = (t << 16) | (t >>> 16);
			INV_SUB_MIX_2[sx] = (t << 8) | (t >>> 24);
			INV_SUB_MIX_3[sx] = t;

			// Compute next counter
			if (!x) {
				x = xi = 1;
			} else {
				x = x2 ^ d[d[d[x8 ^ x2]]];
				xi ^= d[d[xi]];
			}
		}
	}());

	// Precomputed Rcon lookup
	var RCON = [ 0x00, 0x01, 0x02, 0x04, 0x08, 0x10, 0x20, 0x40, 0x80, 0x1b,0x36 ];

	/**
	 * AES block cipher algorithm.
	 */
	var AES = C_algo.AES = BlockCipher.extend({
		_doReset : function() {
			// Shortcuts
			var key = this._key;
			var keyWords = key.words;
			var keySize = key.sigBytes / 4;
			// Compute number of rounds
			var nRounds = this._nRounds = keySize + 6
			// Compute number of key schedule rows
			var ksRows = (nRounds + 1) * 4;
			// Compute key schedule
			var keySchedule = this._keySchedule = [];
			for (var ksRow = 0; ksRow < ksRows; ksRow++) {
				if (ksRow < keySize) {
					keySchedule[ksRow] = keyWords[ksRow];
				} else {
					var t = keySchedule[ksRow - 1];
					if (!(ksRow % keySize)) {
						// Rot word
						t = (t << 8) | (t >>> 24);
						// Sub word
						t = (SBOX[t >>> 24] << 24)
								| (SBOX[(t >>> 16) & 0xff] << 16)
								| (SBOX[(t >>> 8) & 0xff] << 8)
								| SBOX[t & 0xff];
						// Mix Rcon
						t ^= RCON[(ksRow / keySize) | 0] << 24;
					} else if (keySize > 6 && ksRow % keySize == 4) {
						// Sub word
						t = (SBOX[t >>> 24] << 24)
								| (SBOX[(t >>> 16) & 0xff] << 16)
								| (SBOX[(t >>> 8) & 0xff] << 8)
								| SBOX[t & 0xff];
					}

					keySchedule[ksRow] = keySchedule[ksRow - keySize] ^ t;
				}
			}

			// Compute inv key schedule
			var invKeySchedule = this._invKeySchedule = [];
			for (var invKsRow = 0; invKsRow < ksRows; invKsRow++) {
				var ksRow = ksRows - invKsRow;
				if (invKsRow % 4) {
					var t = keySchedule[ksRow];
				} else {
					var t = keySchedule[ksRow - 4];
				}
				if (invKsRow < 4 || ksRow <= 4) {
					invKeySchedule[invKsRow] = t;
				} else {
					invKeySchedule[invKsRow] = INV_SUB_MIX_0[SBOX[t >>> 24]]
							^ INV_SUB_MIX_1[SBOX[(t >>> 16) & 0xff]]
							^ INV_SUB_MIX_2[SBOX[(t >>> 8) & 0xff]]
							^ INV_SUB_MIX_3[SBOX[t & 0xff]];
				}
			}
		},

		encryptBlock : function(M, offset) {
			this._doCryptBlock(M, offset, this._keySchedule, SUB_MIX_0,
					SUB_MIX_1, SUB_MIX_2, SUB_MIX_3, SBOX);
		},

		decryptBlock : function(M, offset) {
			// Swap 2nd and 4th rows
			var t = M[offset + 1];
			M[offset + 1] = M[offset + 3];
			M[offset + 3] = t;
			this._doCryptBlock(M, offset, this._invKeySchedule, INV_SUB_MIX_0,
					INV_SUB_MIX_1, INV_SUB_MIX_2, INV_SUB_MIX_3, INV_SBOX);
			// Inv swap 2nd and 4th rows
			var t = M[offset + 1];
			M[offset + 1] = M[offset + 3];
			M[offset + 3] = t;
		},

		_doCryptBlock : function(M, offset, keySchedule, SUB_MIX_0, SUB_MIX_1,
				SUB_MIX_2, SUB_MIX_3, SBOX) {
			// Shortcut
			var nRounds = this._nRounds;
			// Get input, add round key
			var s0 = M[offset] ^ keySchedule[0];
			var s1 = M[offset + 1] ^ keySchedule[1];
			var s2 = M[offset + 2] ^ keySchedule[2];
			var s3 = M[offset + 3] ^ keySchedule[3];
			// Key schedule row counter
			var ksRow = 4;
			// Rounds
			for (var round = 1; round < nRounds; round++) {
				// Shift rows, sub bytes, mix columns, add round key
				var t0 = SUB_MIX_0[s0 >>> 24] ^ SUB_MIX_1[(s1 >>> 16) & 0xff]
						^ SUB_MIX_2[(s2 >>> 8) & 0xff] ^ SUB_MIX_3[s3 & 0xff]
						^ keySchedule[ksRow++];
				var t1 = SUB_MIX_0[s1 >>> 24] ^ SUB_MIX_1[(s2 >>> 16) & 0xff]
						^ SUB_MIX_2[(s3 >>> 8) & 0xff] ^ SUB_MIX_3[s0 & 0xff]
						^ keySchedule[ksRow++];
				var t2 = SUB_MIX_0[s2 >>> 24] ^ SUB_MIX_1[(s3 >>> 16) & 0xff]
						^ SUB_MIX_2[(s0 >>> 8) & 0xff] ^ SUB_MIX_3[s1 & 0xff]
						^ keySchedule[ksRow++];
				var t3 = SUB_MIX_0[s3 >>> 24] ^ SUB_MIX_1[(s0 >>> 16) & 0xff]
						^ SUB_MIX_2[(s1 >>> 8) & 0xff] ^ SUB_MIX_3[s2 & 0xff]
						^ keySchedule[ksRow++];
				// Update state
				s0 = t0;
				s1 = t1;
				s2 = t2;
				s3 = t3;
			}

			// Shift rows, sub bytes, add round key
			var t0 = ((SBOX[s0 >>> 24] << 24)
					| (SBOX[(s1 >>> 16) & 0xff] << 16)
					| (SBOX[(s2 >>> 8) & 0xff] << 8) | SBOX[s3 & 0xff])
					^ keySchedule[ksRow++];
			var t1 = ((SBOX[s1 >>> 24] << 24)
					| (SBOX[(s2 >>> 16) & 0xff] << 16)
					| (SBOX[(s3 >>> 8) & 0xff] << 8) | SBOX[s0 & 0xff])
					^ keySchedule[ksRow++];
			var t2 = ((SBOX[s2 >>> 24] << 24)
					| (SBOX[(s3 >>> 16) & 0xff] << 16)
					| (SBOX[(s0 >>> 8) & 0xff] << 8) | SBOX[s1 & 0xff])
					^ keySchedule[ksRow++];
			var t3 = ((SBOX[s3 >>> 24] << 24)
					| (SBOX[(s0 >>> 16) & 0xff] << 16)
					| (SBOX[(s1 >>> 8) & 0xff] << 8) | SBOX[s2 & 0xff])
					^ keySchedule[ksRow++];
			// Set output
			M[offset] = t0;
			M[offset + 1] = t1;
			M[offset + 2] = t2;
			M[offset + 3] = t3;
		},

		keySize : 256 / 32
	});

	/**
	 * Shortcut functions to the cipher's object interface.
	 * 
	 * @example
	 * var ciphertext =CryptoJS.AES.encrypt(message, key, cfg); 
	 * var plaintext = CryptoJS.AES.decrypt(ciphertext, key, cfg);
	 */
	C.AES = BlockCipher._createHelper(AES);
}());

(function() {
	// Shortcuts
	var C = CryptoJS;
	var C_lib = C.lib;
	var WordArray = C_lib.WordArray;
	var C_enc = C.enc;
	/**
	 * Base64 encoding strategy.
	 */
	var Base64 = C_enc.Base64 = {
		/**
		 * Converts a word array to a Base64 string.
		 * @param {WordArray} wordArray The word array.
		 * @return {string} The Base64 string.
		 * @static
		 * @example
		 * var base64String = CryptoJS.enc.Base64.stringify(wordArray);
		 */
		stringify : function(wordArray) {
			// Shortcuts
			var words = wordArray.words;
			var sigBytes = wordArray.sigBytes;
			var map = this._map;
			// Clamp excess bits
			wordArray.clamp();
			// Convert
			var base64Chars = [];
			for (var i = 0; i < sigBytes; i += 3) {
				var byte1 = (words[i >>> 2] >>> (24 - (i % 4) * 8)) & 0xff;
				var byte2 = (words[(i + 1) >>> 2] >>> (24 - ((i + 1) % 4) * 8)) & 0xff;
				var byte3 = (words[(i + 2) >>> 2] >>> (24 - ((i + 2) % 4) * 8)) & 0xff;
				var triplet = (byte1 << 16) | (byte2 << 8) | byte3;
				for (var j = 0; (j < 4) && (i + j * 0.75 < sigBytes); j++) {
					base64Chars.push(map.charAt((triplet >>> (6 * (3 - j))) & 0x3f));
				}
			}
			// Add padding
			var paddingChar = map.charAt(64);
			if (paddingChar) {
				while (base64Chars.length % 4) {
					base64Chars.push(paddingChar);
				}
			}
			return base64Chars.join('');
		},

		/**
		 * Converts a Base64 string to a word array.
		 * @param {string} base64Str The Base64 string.
		 * @return {WordArray} The word array.
		 * @static
		 * @example
		 * var wordArray = CryptoJS.enc.Base64.parse(base64String);
		 */
		parse : function(base64Str) {
			// Shortcuts
			var base64StrLength = base64Str.length;
			var map = this._map;
			// Ignore padding
			var paddingChar = map.charAt(64);
			if (paddingChar) {
				var paddingIndex = base64Str.indexOf(paddingChar);
				if (paddingIndex != -1) {
					base64StrLength = paddingIndex;
				}
			}
			// Convert
			var words = [];
			var nBytes = 0;
			for (var i = 0; i < base64StrLength; i++) {
				if (i % 4) {
					var bits1 = map.indexOf(base64Str.charAt(i - 1)) << ((i % 4) * 2);
					var bits2 = map.indexOf(base64Str.charAt(i)) >>> (6 - (i % 4) * 2);
					words[nBytes >>> 2] |= (bits1 | bits2) << (24 - (nBytes % 4) * 8);
					nBytes++;
				}
			}
			return WordArray.create(words, nBytes);
		},
		_map : 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/='
	};
}());

/**
 * jQuery JSON plugin 2.4.0
 * 
 * @author Brantley Harris, 2009-2011
 * @author Timo Tijhof, 2011-2012
 * @source This plugin is heavily influenced by MochiKit's serializeJSON, which
 *         is copyrighted 2005 by Bob Ippolito.
 * @source Brantley Harris wrote this plugin. It is based somewhat on the
 *         JSON.org website's http://www.json.org/json2.js, which proclaims: "NO
 *         WARRANTY EXPRESSED OR IMPLIED. USE AT YOUR OWN RISK.", a sentiment that I uphold.
 * @license MIT License <http://www.opensource.org/licenses/mit-license.php>
 */
(function($) {
	var escape = /["\\\x00-\x1f\x7f-\x9f]/g, meta = {
		'\b' : '\\b',
		'\t' : '\\t',
		'\n' : '\\n',
		'\f' : '\\f',
		'\r' : '\\r',
		'"' : '\\"',
		'\\' : '\\\\'
	}, hasOwn = Object.prototype.hasOwnProperty;

	/**
	 * jQuery.toJSON Converts the given argument into a JSON representation.
	 * 
	 * @param o {Mixed} The json-serializable *thing* to be converted
	 * If an object has a toJSON prototype, that will be used to get the
	 * representation. Non-integer/string keys are skipped in the object, as are
	 * keys that point to a function.
	 */
	$.toJSON = typeof JSON === 'object' && JSON.stringify ? JSON.stringify: function(o) {
				if (o === null) {
					return 'null';
				}
				var pairs, k, name, val, type = $.type(o);
				if (type === 'undefined') {
					return undefined;
				}

				// Also covers instantiated Number and Boolean objects,
				// which are typeof 'object' but thanks to $.type, we
				// catch them here. I don't know whether it is right
				// or wrong that instantiated primitives are not
				// exported to JSON as an {"object":..}.
				// We choose this path because that's what the browsers did.
				if (type === 'number' || type === 'boolean') {
					return String(o);
				}
				if (type === 'string') {
					return $.quoteString(o);
				}
				if (typeof o.toJSON === 'function') {
					return $.toJSON(o.toJSON());
				}
				if (type === 'date') {
					var month = o.getUTCMonth() + 1, day = o.getUTCDate(), year = o
							.getUTCFullYear(), hours = o.getUTCHours(), minutes = o
							.getUTCMinutes(), seconds = o.getUTCSeconds(), milli = o
							.getUTCMilliseconds();

					if (month < 10) {
						month = '0' + month;
					}
					if (day < 10) {
						day = '0' + day;
					}
					if (hours < 10) {
						hours = '0' + hours;
					}
					if (minutes < 10) {
						minutes = '0' + minutes;
					}
					if (seconds < 10) {
						seconds = '0' + seconds;
					}
					if (milli < 100) {
						milli = '0' + milli;
					}
					if (milli < 10) {
						milli = '0' + milli;
					}
					return '"' + year + '-' + month + '-' + day + 'T' + hours
							+ ':' + minutes + ':' + seconds + '.' + milli + 'Z"';
				}
				pairs = [];
				if ($.isArray(o)) {
					for (k = 0; k < o.length; k++) {
						pairs.push($.toJSON(o[k]) || 'null');
					}
					return '[' + pairs.join(',') + ']';
				}

				// Any other object (plain object, RegExp, ..)
				// Need to do typeof instead of $.type, because we also
				// want to catch non-plain objects.
				if (typeof o === 'object') {
					for (k in o) {
						// Only include own properties,
						// Filter out inherited prototypes
						if (hasOwn.call(o, k)) {
							// Keys must be numerical or string. Skip others
							type = typeof k;
							if (type === 'number') {
								name = '"' + k + '"';
							} else if (type === 'string') {
								name = $.quoteString(k);
							} else {
								continue;
							}
							type = typeof o[k];

							// Invalid values like these return undefined
							// from toJSON, however those object members
							// shouldn't be included in the JSON string at all.
							if (type !== 'function' && type !== 'undefined') {
								val = $.toJSON(o[k]);
								pairs.push(name + ':' + val);
							}
						}
					}
					return '{' + pairs.join(',') + '}';
				}
			};

	/**
	 * jQuery.evalJSON Evaluates a given json string.
	 * 
	 * @param str {String}
	 */
	$.evalJSON = typeof JSON === 'object' && JSON.parse ? JSON.parse : function(str) {
				/* jshint evil: true */
				return eval('(' + str + ')');
	};

	/**
	 * jQuery.secureEvalJSON Evals JSON in a way that is *more* secure.
	 * @param str {String}
	 */
	$.secureEvalJSON = typeof JSON === 'object' && JSON.parse ? JSON.parse
			: function(str) {
				var filtered = str.replace(/\\["\\\/bfnrtu]/g, '@')
								  .replace(/"[^"\\\n\r]*"|true|false|null|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?/g,']')
								  .replace(/(?:^|:|,)(?:\s*\[)+/g, '');
				if (/^[\],:{}\s]*$/.test(filtered)) {
					/* jshint evil: true */
					return eval('(' + str + ')');
				}
				throw new SyntaxError('Error parsing JSON, source is not valid.');
			};

	/**
	 * jQuery.quoteString Returns a string-repr of a string, escaping quotes
	 * intelligently. Mostly a support function for toJSON. Examples: >>>
	 * jQuery.quoteString('apple') "apple" >>> 
	 * jQuery.quoteString('"Where are we going?", she asked.') "\"Where are we going?\", she asked."
	 */
	$.quoteString = function(str) {
		if (str.match(escape)) {
			return '"' + str.replace(escape, function(a) {
				var c = meta[a];
				if (typeof c === 'string') {
					return c;
				}
				c = a.charCodeAt();
				return '\\u00' + Math.floor(c / 16).toString(16) + (c % 16).toString(16);
			}) + '"';
		}
		return '"' + str + '"';
	};

}(jQuery));

/**
 * corecode namespace
 */
var corecode = {};
/*******************************************************************************
 * Cookie 管理
 */
corecode.cookie = {};
(function(a){if(typeof define==="function"&&define.amd){define(["jquery"],a)}else{if(typeof exports==="object"){a(require("jquery"))}else{a(jQuery)}}}(function(f){var a=/\+/g;function d(i){return b.raw?i:encodeURIComponent(i)}function g(i){return b.raw?i:decodeURIComponent(i)}function h(i){return d(b.json?JSON.stringify(i):String(i))}function c(i){if(i.indexOf('"')===0){i=i.slice(1,-1).replace(/\\"/g,'"').replace(/\\\\/g,"\\")}try{i=decodeURIComponent(i.replace(a," "));return b.json?JSON.parse(i):i}catch(j){}}function e(j,i){var k=b.raw?j:c(j);return f.isFunction(i)?i(k):k}var b=f.cookie=function(q,p,w){if(p!==undefined&&!f.isFunction(p)){w=f.extend({},b.defaults,w);if(typeof w.expires==="number"){var r=w.expires,u=w.expires=new Date();u.setTime(+u+r*60000)}var v="; path="+w.path;return(document.cookie=[d(q),"=",h(p),w.expires?"; expires="+w.expires.toUTCString():"",w.path?v:"",w.domain?"; domain="+w.domain:"",w.secure?"; secure":""].join(""))}var x=q?undefined:{};var s=document.cookie?document.cookie.split("; "):[];for(var o=0,m=s.length;o<m;o++){var n=s[o].split("=");var j=g(n.shift());var k=n.join("=");if(q&&q===j){x=e(k,p);break}if(!q&&(k=e(k))!==undefined){x[j]=k}}return x};b.defaults={};f.removeCookie=function(j,i){if(f.cookie(j)===undefined){return false}f.cookie(j,"",f.extend({},i,{expires:-1}));return !f.cookie(j)}}));
/**
 * 增加和修改 Cookie（jQuery.cookie）
 */
corecode.cookie.add = function(name, value, options) {
	if(typeof value==="object"){
		var wrkStr=encodeURIComponent($.toJSON(value));
		$.cookie(name,wrkStr, options);
	}else if(value){
		var wrkStr=encodeURIComponent(value);
		$.cookie(name,wrkStr, options);
	}
};

/**
 * 删除 Cookie
 */
corecode.cookie.del = function(name,options) {
	return $.removeCookie(name,options);
};

/**
 * 清空 Cookie
 */
corecode.cookie.empty = function(name) {
	var rs = document.cookie.match(new RegExp("([^ ;][^;]*)(?=(=[^;]*)(;|$))","gi"));
	for ( var i in rs) {
		corecode.cookie.del(rs[i]);
	}
};

/**
 * 获取Cookie
 */
corecode.cookie.get = function(name) {
	if($.trim($.cookie(name))==""){
		return null;
	}
	return decodeURIComponent($.cookie(name));
};

/**
 * 启用Cookie？
 */
corecode.cookie.isReady = function() {
	return navigator.cookieEnabled;
};

/*******************************************************************************
 * 安全管理
 */
corecode.security = {};
/**
 * BASE64 编码(input: wordArray, output: BASE64Text)
 */
corecode.security.BASE64 = {};
corecode.security.BASE64.encodeWordArray = function(data) {
	return CryptoJS.enc.Base64.stringify(data);
};
/**
 * BASE64 解码（input: BASE64Text, output: wordArray）
 */
corecode.security.BASE64.decodeWordArray = function(str) {
	// BUG: Remove \r \n
	var wrkStr = str.replace(/\r/g, "");
	wrkStr = wrkStr.replace(/\n/g, "");
	return CryptoJS.enc.Base64.parse(wrkStr);
};

/**
 * BASE64 编码(input: UTF8Text, output: BASE64Text)
 */
corecode.security.BASE64.encode = function(utf8Str) {
	var wordData = CryptoJS.enc.Utf8.parse(utf8Str);
	return corecode.security.BASE64.encodeWordArray(wordData);
};
/**
 * BASE64 解码（input: BASE64Text, output: UTF8Text）
 */
corecode.security.BASE64.decode = function(str) {
	// BUG: Remove \r \n
	var wrkStr = str.replace(/\r/g, "");
	wrkStr = wrkStr.replace(/\n/g, "");
	var wordData = corecode.security.BASE64.decodeWordArray(wrkStr);
	return CryptoJS.enc.Utf8.stringify(wordData);
};

/**
 * AES 加密（key, iv, input: UTF8Text, output: wordArray）
 */
corecode.security.AES = {};
corecode.security.AES.encryption = function(key, iv, orgData) {
	var keyData = CryptoJS.enc.Utf8.parse(key);
	var ivData = CryptoJS.enc.Utf8.parse(iv);
	var encData = CryptoJS.AES.encrypt(orgData, keyData, {
		mode : CryptoJS.mode.CBC,
		iv : ivData,
		padding : CryptoJS.pad.Pkcs7
	});
	return encData.ciphertext;
};

/**
 * AES 解密（input: wordArray, output: wordArray）
 */
corecode.security.AES.decryption = function(key, iv, encData) {
	var keyData = CryptoJS.enc.Utf8.parse(key);
	var ivData = CryptoJS.enc.Utf8.parse(iv);
	var wrkData = CryptoJS.lib.CipherParams.create({
		ciphertext : encData
	});
	var orgData = CryptoJS.AES.decrypt(wrkData, keyData, {
		mode : CryptoJS.mode.CBC,
		iv : ivData,
		padding : CryptoJS.pad.Pkcs7
	});
	return orgData;
};

/**
 * AES 加密（input: UTF8Text, output: BASE64Text）
 */
corecode.security.AES.encode = function(key, iv, orgData) {
	var encData = corecode.security.AES.encryption(key, iv, orgData);
	return corecode.security.BASE64.encodeWordArray(encData);
};

/**
 * AES 解密（input: BASE64Text, output: UTF8Text）
 */
corecode.security.AES.decode = function(key, iv, encData) {
	var wrkData = corecode.security.BASE64.decodeWordArray(encData);
	var orgData = corecode.security.AES.decryption(key, iv, wrkData);
	return CryptoJS.enc.Utf8.stringify(orgData);
};